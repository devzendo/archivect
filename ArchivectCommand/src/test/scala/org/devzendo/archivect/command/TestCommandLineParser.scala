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

import scala.collection.mutable.ListBuffer
import org.scalatest.junit.{ AssertionsForJUnit, MustMatchersForJUnit }
import org.junit.Assert._
import org.junit.Test
import org.junit.Before
import org.devzendo.archivect.command.CommandModel.CommandMode._
import org.junit.Ignore

class TestCommandLineParser extends AssertionsForJUnit with MustMatchersForJUnit {
    @Test
    def nonVerboseByDefault() {
        val model = parse("-archive irrelevantsource") // -archive since a mode must be specified
        assertFalse(model.verbose)
    }

    @Test
    def verboseCanBeSpecified() {
        val model = parse("-archive -v irrelevantsource") // -archive since a mode must be specified
        assertTrue(model.verbose)
    }

    @Test
    def aModeMustBeSpecified() {
        val ex = intercept[CommandLineException] {
            parse("-irrelevant irrelevantsource")
        }
        ex.getMessage() must equal("A mode must be specified")
    }

    @Test
    def allModesAreAccepted() {
        CommandModel.CommandMode.values.foreach {
            validMode =>
                val modeArgumentString = "-" + validMode.toString().toLowerCase()
                parse(modeArgumentString + " irrelevantsource").mode must equal(Some(validMode))
        }
    }
    
    @Test
    def cannotSpecifyMoreThanOneMode() {
        val ex = intercept[CommandLineException] {
            parse("-archive -restore irrelevantsource")
        }
        ex.getMessage() must equal("Cannot set the mode multiple times")
    }
    
    @Test
    def archiveModeMustHaveSources() {
        val ex = intercept[CommandLineException] {
            parse("-archive")
        }
        ex.getMessage() must equal("One or more sources must be specified")
    }
    
    private def parse(line: String): CommandModel = {
        val parser = new CommandLineParser()
        parser.parse(split(line))
    }

    private def split(line: String): java.util.List[String] = {
        val wordList = new java.util.ArrayList[String]()
        line.split(" ") foreach { wordList.add(_) }
        wordList
    }
}