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


class TestRuleCompiler extends AssertionsForJUnit with MustMatchersForJUnit {
    val compiler = new RuleCompiler()

    @Test
    def globRuleReturnsGlobRulePredicate() {
        val predicate = compiler.compile(Rule(Glob, "*.c", "/tmp"))
        predicate.isInstanceOf[GlobRulePredicate] must be(true)
    }

    @Test
    def regexRuleReturnsGlobRulePredicate() {
        val predicate = compiler.compile(Rule(Regex, "^*\\.c$", "/tmp"))
        predicate.isInstanceOf[RegexRulePredicate] must be(true)
    }

    @Test
    def iregexRuleReturnsGlobRulePredicate() {
        val predicate = compiler.compile(Rule(IRegex, "^*\\.c$", "/tmp"))
        predicate.isInstanceOf[IRegexRulePredicate] must be(true)
    }

    @Test
    def filetypeRuleReturnsGlobRulePredicate() {
        val predicate = compiler.compile(Rule(FileType, "f", "/tmp"))
        predicate.isInstanceOf[FileTypeRulePredicate] must be(true)
    }
}