package org.devzendo.archivect.rule

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

import org.scalatest.junit.{AssertionsForJUnit, MustMatchersForJUnit}
import org.junit.{Test}

import org.devzendo.archivect.model.Rule
import org.devzendo.archivect.model.CommandModel.RuleType._

class TestGlobRuleCompiler extends AssertionsForJUnit with MustMatchersForJUnit {
    val compiler = new RuleCompiler()

    @Test
    def globRulePredicateMatchesCorrectly() {
        val predicate = compiler.compile(Rule(Glob, "*.c", "/tmp"))
        val cFile = StubDetailedFile("/tmp/foo.c")
        predicate.matches(cFile) must be(true)
        val txtFile = StubDetailedFile("/tmp/foo.txt")
        predicate.matches(txtFile) must be(false)
    }

    @Test
    def globIsCaseSensitive() {
        val predicate = compiler.compile(Rule(Glob, "*.c", "/tmp"))
        val caseMismatchingFile = StubDetailedFile("/tmp/foo.C")
        predicate.matches(caseMismatchingFile) must be(false)
    }
}