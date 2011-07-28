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

import org.devzendo.archivect.command.CommandModel.CommandMode._

class CommandLineParser {
    private abstract class ModeSpecificParser(val model: CommandModel) {
        def parse(specificArgs: List[String]): Unit
    }
    private class UnknownModeParser(model: CommandModel) extends ModeSpecificParser(model) {
        def parse(specificArgs: List[String]) = {
            
        }
    }
    private class ArchiveSpecificParser(model: CommandModel) extends ModeSpecificParser(model) {
        def parse(specificArgs: List[String]) = {
            
        }
    }
    @throws(classOf[CommandLineException])
    def parse(inputLine: java.util.List[String]): CommandModel = {
        val model = new CommandModel()
        def disallowMultipleModes() = {
            
        }
        var modeSpecificParser = new UnknownModeParser(model)
        try {
            for (arg <- inputLine) {
                println("args is '" + arg + "'")
                arg match {
                    case "-v" | "-verbose" =>
                        model.verbose = true
                    case "-a" | "-archive" =>
                        model.mode = CommandModel.CommandMode.Archive
                    case "-r" | "-restore" =>
                        model.mode = CommandModel.CommandMode.Restore
                    case "-b" | "-backup" =>
                        model.mode = CommandModel.CommandMode.Backup
                    case "-d" | "-verify" =>
                        model.mode = CommandModel.CommandMode.Verify
                    case _ =>
                    // something else
                }
            }
        } catch {
            case ill: IllegalStateException => throw new CommandLineException(ill.getMessage())
        }

        if (model.mode == None) {
            throw new CommandLineException("A mode must be specified")
        }
        
        model
    }
    
}