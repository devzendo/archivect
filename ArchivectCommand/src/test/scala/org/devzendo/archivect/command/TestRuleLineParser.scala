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
import scala.util.parsing.combinator._
import org.scalatest.junit.{ AssertionsForJUnit, MustMatchersForJUnit }
import org.junit.Assert._
import org.junit.Test
import org.junit.Before
import org.devzendo.archivect.command.CommandModel.CommandMode._
import org.devzendo.archivect.command.CommandModel.Encoding._
import org.devzendo.archivect.command.CommandModel.Compression._
import org.devzendo.archivect.command.CommandModel.RuleType._
import org.junit.Ignore

class TestRuleLineParser extends AssertionsForJUnit with MustMatchersForJUnit {
    val parser = new RuleLineParser()
    
    @Test
    def blankLineGivesNone() {
        parser.parseLine("    ") must be (None)
    }
    
    @Test
    def commentAtStartOfLineGivesNone() {
        parser.parseLine("# comment    ") must be (None)
    }

    @Test
    def commentNotAtStartOfLineGivesNone() {
        parser.parseLine("     # comment    ") must be (None)
    }
    
    @Test
    def goodGlobRuleIsParsed() {
        val expected: Tuple2[Boolean, Rule] = (true, Rule(Glob, "*.c", "/tmp")) 
        parser.parseLine("+ glob *.c /tmp") must be (Some(expected))
    }
    
    @Test
    def goodRegexRuleIsParsed() {
        val expected: Tuple2[Boolean, Rule] = (false, Rule(Regex, "^.*\\.c$", "/tmp")) 
        parser.parseLine("- regex ^.*\\.c$ /tmp") must be (Some(expected))
    }

    @Test
    def quotesAreAllowedInRuleText() {
        // TODO the cases where slash-escaped control chars or \\ are allowed
        val expected: Tuple2[Boolean, Rule] = (false, Rule(Regex, "^spaces in name.*.c$", "/tmp")) 
        parser.parseLine("""- regex "^spaces in name.*.c$" /tmp""") must be (Some(expected))
    }

    @Test
    def quotesAreAllowedInRuleAt() {
        val expected: Tuple2[Boolean, Rule] = (false, Rule(Glob, "spaces*.*", "/tmp directory")) 
        parser.parseLine("""- glob spaces*.* "/tmp directory"""") must be (Some(expected))
    }

    @Test
    def badInclusionFlagThrows() {
        parseFailsWithMessage("$ glob *.c /tmp", "Unknown rule inclusion/exclusion type '$'")
    }
    
    @Test
    def badRuleTypeThrows() {
        parseFailsWithMessage("+ vagueness *.cpp,*.h /some/subfolder", "Unknown rule type 'vagueness'")
    }

    private def parseFailsWithMessage(ruleLine: String, message: String) = {
        val ex = intercept[IllegalStateException] {
            parser.parseLine(ruleLine)
        }
        ex.getMessage() must equal(message)
    }

}