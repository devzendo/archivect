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

import java.util.List
import scala.collection.JavaConversions._
import scala.util.parsing.combinator._
import scala.collection.mutable.ArrayBuffer

import org.devzendo.archivect.command.CommandModel.CommandMode._

class CommandLineParser {
    private abstract class ModeSpecificParser(val model: CommandModel) {
        def parse(currentArg: String, args: Iterator[String]): Unit
        def validate: Unit
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
                    // TODO
                case _ => 
                    model.addSource(currentArg)
            }
        }
        def validate = {
            if (model.sources.isEmpty) {
                throw new IllegalStateException("One or more sources must be specified")
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
        var modeSpecificParser: ModeSpecificParser = new UnknownModeParser(model)

        try {
            val inputBuffer: scala.collection.mutable.Buffer[String] = inputLine
            val inputIterator = inputBuffer.iterator
            while (inputIterator.hasNext) {
                val arg = inputIterator.next()
                println("arg is '" + arg + "'")
                arg match {
                    case "-v" | "-verbose" =>
                        model.verbose = true
                    case "-a" | "-archive" =>
                        model.mode = CommandModel.CommandMode.Archive
                        modeSpecificParser = new ArchiveSpecificParser(model)
                    case "-r" | "-restore" =>
                        model.mode = CommandModel.CommandMode.Restore
                        modeSpecificParser = new RestoreSpecificParser(model)
                    case "-b" | "-backup" =>
                        model.mode = CommandModel.CommandMode.Backup
                        modeSpecificParser = new BackupSpecificParser(model)
                    case "-y" | "-verify" =>
                        model.mode = CommandModel.CommandMode.Verify
                        modeSpecificParser = new VerifySpecificParser(model)
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