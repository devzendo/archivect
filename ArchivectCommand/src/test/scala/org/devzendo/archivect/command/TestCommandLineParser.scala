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

//import org.devzendo.archivect.command.CommandModel.CommandMode._

import org.devzendo.archivect.command.CommandModel.CommandMode._

class TestCommandLineParser extends AssertionsForJUnit with MustMatchersForJUnit  {
    @Test
    def nonVerbose() = {
        val model = parse("-archive") // -archive since a mode must be specified
        assertFalse(model.verbose)
    }

    @Test
    def verbose() = {
        val model = parse("-archive -v") // -archive since a mode must be specified
        assertTrue(model.verbose)
    }
    
    @Test(expected = classOf[CommandLineException])
    def aModeMustBeSpecified() {
        parse("-irrelevant")
    }
    
    @Test
    def archive() = {
        parse("-archive").mode must equal(Archive)
    }
    private def parse(line: String): CommandModel = {
        val parser = new CommandLineParser()
        parser.parse(split(line))
    }

    private def split(line: String): java.util.List[String] = {
        val wordList = new java.util.ArrayList[String]()
        line.split(" ") foreach { wordList.add(_) }
        wordList
        //      val wordsList = java.util.Arrays.
        //      new java.util.ArrayList[String]().words)
    }
}