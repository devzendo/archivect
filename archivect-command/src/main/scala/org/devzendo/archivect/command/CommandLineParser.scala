/**
 * Copyright (C) 2008-2011 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.archivect.command

import java.io.{ IOException, FileNotFoundException }
import java.util.List

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.util.parsing.combinator._

import org.devzendo.archivect.model.{ CommandModel, Rule }
import org.devzendo.archivect.model.CommandModel.CommandMode._
import org.devzendo.archivect.model.CommandModel.Encoding._
import org.devzendo.archivect.model.CommandModel.Compression._
import org.devzendo.archivect.model.CommandModel.RuleType._

class CommandLineParser {
    // TODO remove duplication of the RuleType strings here, and in RuleLineParser
    private object RuleParser {
        def parse(ruleType: String, ruleText: String, ruleDirectory: String): Rule = {
            val ruleTypeTrimmed = ruleType.toLowerCase().trim()
            ruleTypeTrimmed match {
                case "glob" =>
                    new Rule(Glob, ruleText, ruleDirectory)
                case "regex" =>
                    new Rule(Regex, ruleText, ruleDirectory)
                case "iregex" =>
                    new Rule(IRegex, ruleText, ruleDirectory)
                case "filetype" | "type" =>
                    new Rule(FileType, ruleText, ruleDirectory)
                case _ =>
                    throw new IllegalStateException("Unknown rule type '" + ruleTypeTrimmed + "'")
            }
        }
    }
    
    private abstract class ModeSpecificParser(val model: CommandModel) {
        def parse(currentArg: String, args: Iterator[String]): Unit
        def validate: Unit
        def getNext(args: Iterator[String], failMessage: String, f: String => Unit): Unit = {
            if (args.hasNext) {
                f(args.next())
            } else {
                throw new IllegalStateException(failMessage)
            }
        }
        def getNext2(args: Iterator[String], failMessage: String, f: (String, String) => Unit): Unit = {
            if (args.hasNext) {
                val str1 = args.next()
                if (args.hasNext) {
                    val str2 = args.next()
                    f(str1, str2)
                    return
                }
            }
            throw new IllegalStateException(failMessage)
        }
        def getNext3(args: Iterator[String], failMessage: String, f: (String, String, String) => Unit): Unit = {
            if (args.hasNext) {
                val str1 = args.next()
                if (args.hasNext) {
                    val str2 = args.next()
                    if (args.hasNext) {
                        val str3 = args.next
                        f(str1, str2, str3)
                        return
                    }
                }
            }
            throw new IllegalStateException(failMessage)
        }
    }

    private class UnknownModeParser(model: CommandModel) extends ModeSpecificParser(model) {
        def parse(currentArg: String, args: Iterator[String]) = {
            
        }
        def validate = {
            
        }
    }

    private class SourceValidator(model: CommandModel) extends ModeSpecificParser(model) {
        def parse(currentArg: String, args: Iterator[String]) = {
            currentArg match {
                case "-d" | "-destination" =>
                    getNext(args, "A destination must be given, following -destination", { model.destination = _ })
                case "-n" | "-name" =>
                    getNext(args, "A name must be given, following -name", { model.name = _ })
                case "-e" | "-encoding" =>
                    getNext(args, "An encoding must be given, following -encoding", { setEncodingAndCompression(_) })
                case "-x" | "-exclude" =>
                    getNext(args, "An exclusion must be given, following -exclude", { model.addExclusion(_) })
                case "-X" | "-excludefrom" =>
                    getNext(args, "An exclusions file must be given, following -excludefrom", { addExclusionsFromFile(_) })
                case "-r" | "-rule" =>
                    getNext3(args, "A rule exclusion must be of the form -rule <ruletype> <ruletext> <ruledirectory>", { addRuleExclusion(_, _, _)})
                case "+r" | "+rule" =>
                    getNext3(args, "A rule inclusion must be of the form +rule <ruletype> <ruletext> <ruledirectory>", { addRuleInclusion(_, _, _)})
                case "-R" | "-rulefrom" =>
                    getNext(args, "A rules file must be given, following -rulefrom", { addRulesFromFile(_)})
                case _ => 
                    model.addSource(currentArg)
            }
        }

        private def addExclusionsFromFile(exclusionFile: String) = {
            try {
                 Source.fromFile(exclusionFile).getLines.map(_.trim).filter(_.length() > 0).foreach(ex => model.addExclusion(ex))
            } catch {
                case fnf: FileNotFoundException => throw new IllegalStateException("The exclusions file '" + exclusionFile + "' does not exist") 
                case ioe: IOException => throw new IllegalStateException("The exclusions file '" + exclusionFile + "' cannot be read: " + ioe.getMessage()) 
            }
        }

        private def addRuleExclusion(ruleType: String, ruleText: String, ruleAt: String): Unit = {
            model.addRuleExclusion(RuleParser.parse(ruleType, ruleText, ruleAt))
        }

        private def addRuleInclusion(ruleType: String, ruleText: String, ruleAt: String): Unit = {
            model.addRuleInclusion(RuleParser.parse(ruleType, ruleText, ruleAt))
        }
        
        private def addRulesFromFile(rulesFile: String): Unit = {
            val insertRuleIntoModel = (isInclusion: Boolean, rule: Rule) => {
                if (isInclusion) {
                    model.addRuleInclusion(rule)
                } else {
                    model.addRuleExclusion(rule)
                }
            } 
            try {
                val ruleLineParser = new RuleLineParser()
                Source.fromFile(rulesFile).getLines.map(_.trim).filter(_.length() > 0).foreach( (line) => {
                    val optionalRule: Option[Tuple2[Boolean, Rule]] = ruleLineParser.parseLine(line)
                    // I'd like to do this with foreach, but can't quite express it.
                    if (optionalRule.isDefined) {
                        val detail = optionalRule.get
                        insertRuleIntoModel(detail._1, detail._2)
                    }
                })
            } catch {
                case fnf: FileNotFoundException => throw new IllegalStateException("The rules file '" + rulesFile + "' does not exist")
                case ioe: IOException => throw new IllegalStateException("The rules file '" + rulesFile + "' cannot be read: " + ioe.getMessage())
            }
        }
        
        def validate = {
            if (model.sources.isEmpty) {
                throw new IllegalStateException("One or more sources must be specified")
            }
            if (model.destination == None) {
                throw new IllegalStateException("A destination must be specified")
            }
            if (model.name == None) {
                throw new IllegalStateException("A name must be specified")
            }
            if (model.encoding == None) {
                throw new IllegalStateException("An encoding must be specified")
            }
        }
        
        private [this] def setEncodingAndCompression(encString: String) = {
            val splitter = """\.?(tar|tgz|tbz|zip|aar)(?:\.(gz|bz))?""".r
            encString.toLowerCase() match {
                case splitter(format, compression) =>
                    format match {
                        case "tar" =>
                            model.encoding = CommandModel.Encoding.Tar
                        case "tgz" =>
                            model.encoding = CommandModel.Encoding.Tar
                            model.compression = CommandModel.Compression.Gzip
                        case "tbz" =>
                            model.encoding = CommandModel.Encoding.Tar
                            model.compression = CommandModel.Compression.Bzip
                        case "zip" =>
                            model.encoding = CommandModel.Encoding.Zip
                        case "aar" =>
                            model.encoding = CommandModel.Encoding.Aar
                    }
                    compression match {
                        case "gz" =>
                            model.compression = CommandModel.Compression.Gzip
                        case "bz" =>
                            model.compression = CommandModel.Compression.Bzip
                        case null =>
                            // default is None
                    }
                case _ =>
                    throw new IllegalStateException("Unknown encoding '" + encString + "'")
            }
        }
    }
    
    private class ArchiveSpecificParser(model: CommandModel) extends ModeSpecificParser(model) {
        val sourceValidator = new SourceValidator(model)
        def parse(currentArg: String, args: Iterator[String]) = {
            sourceValidator.parse(currentArg, args)
            // Archive-specific options...
        }
        def validate = {
            sourceValidator.validate
        }
    }
    
    private class BackupSpecificParser(model: CommandModel) extends ModeSpecificParser(model) {
        val sourceValidator = new SourceValidator(model)
        def parse(currentArg: String, args: Iterator[String]) = {
            sourceValidator.parse(currentArg, args)
            // Backup-specific options...
        }
        def validate = {
            sourceValidator.validate
        }
    }
    
    private class RestoreSpecificParser(model: CommandModel) extends ModeSpecificParser(model) {
        def parse(currentArg: String, args: Iterator[String]) = {
            
        }
        def validate = {
            
        }
    }
    
    private class VerifySpecificParser(model: CommandModel) extends ModeSpecificParser(model) {
        def parse(currentArg: String, args: Iterator[String]) = {
            
        }
        def validate = {
            
        }
    }
    
    @throws(classOf[CommandLineException])
    def parse(inputLine: java.util.List[String]): CommandModel = {
        val model = new CommandModel()
        val unknownModeParser = new UnknownModeParser(model)
        var modeSpecificParser: ModeSpecificParser = unknownModeParser

        try {
            val inputBuffer: scala.collection.mutable.Buffer[String] = inputLine
            val inputIterator = inputBuffer.iterator
            while (inputIterator.hasNext) {
                val arg = inputIterator.next()
                arg match {
                    case "-v" | "-verbose" =>
                        model.verbose = true
                    case "-archive" =>
                        model.mode = CommandModel.CommandMode.Archive
                        modeSpecificParser = new ArchiveSpecificParser(model)
                    case "-restore" =>
                        model.mode = CommandModel.CommandMode.Restore
                        modeSpecificParser = new RestoreSpecificParser(model)
                    case "-backup" =>
                        model.mode = CommandModel.CommandMode.Backup
                        modeSpecificParser = new BackupSpecificParser(model)
                    case "-verify" =>
                        model.mode = CommandModel.CommandMode.Verify
                        modeSpecificParser = new VerifySpecificParser(model)
                    case "-?" | "-h" | "-help" =>
                        model.mode = CommandModel.CommandMode.Help
                        modeSpecificParser = unknownModeParser
                    case "-V" | "-version" =>
                        model.mode = CommandModel.CommandMode.Version
                        modeSpecificParser = unknownModeParser
                    case _ =>
                        modeSpecificParser.parse(arg, inputIterator)
                }
            }
            modeSpecificParser.validate
        } catch {
            case ill: IllegalStateException => throw new CommandLineException(ill.getMessage())
        }

        if (model.mode == None) {
            throw new CommandLineException("A mode must be specified")
        }
        
        model
    }
}