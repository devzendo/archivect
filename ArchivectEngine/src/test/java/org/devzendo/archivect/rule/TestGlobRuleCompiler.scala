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
 
package org.devzendo.archivect.rule

import org.scalatest.junit.{ AssertionsForJUnit, MustMatchersForJUnit }
import org.junit.{ Test, Ignore }
import org.jmock.integration.junit4.{ JMock, JUnit4Mockery }

import java.io.File

import org.devzendo.xpfsa.{ DetailedFile, FileStatus }

import org.devzendo.archivect.model.Rule
import org.devzendo.archivect.model.CommandModel.RuleType._

class TestGlobRuleCompiler extends AssertionsForJUnit with MustMatchersForJUnit {
    val compiler = new RuleCompiler()
    
    @Test
    def globRulePredicateMatchesCorrectly() {
        val predicate = compiler.compile(Rule(Glob, "*.c", "/tmp"))
        val cFile = createDetailedFile("/tmp/foo.c")
        predicate.matches(cFile) must be (true)
        val txtFile = createDetailedFile("/tmp/foo.txt")
        predicate.matches(txtFile) must be (false)
    }
    
    private case class StubDetailedFile(val file: File) extends DetailedFile {
        def getFile: File = file
        def getLinkDetailedFile: DetailedFile = null
        def getFileStatus: FileStatus = null
    }

    private def createDetailedFile(fileName: String): DetailedFile = {
        val file = new File(fileName)
        StubDetailedFile(file)
    }
}