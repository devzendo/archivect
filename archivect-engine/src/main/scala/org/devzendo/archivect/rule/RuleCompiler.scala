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

import org.devzendo.archivect.model.Rule
import org.devzendo.archivect.model.CommandModel.RuleType._

private trait RulePredicateCompiler {
    def compile(rule: Rule): RulePredicate
}

class RuleCompiler {
    def compile(rule: Rule): RulePredicate = {
        val compiler = getCompiler(rule.ruleType)
        compiler.compile(rule)
    }
    
    private def getCompiler(ruleType: RuleType): RulePredicateCompiler = {
        ruleType match {
            case Glob => GlobRuleCompiler
            case Regex => RegexRuleCompiler
            case IRegex => IRegexRuleCompiler
            case FileType => FileTypeRuleCompiler
            case _ => throw new UnsupportedOperationException("Cannot compile rule type " + ruleType)
        }
    }
    
    private object GlobRuleCompiler extends RulePredicateCompiler {
        def compile(rule: Rule): RulePredicate = {
            new GlobRulePredicate(rule)
        }
    }
    
    private object RegexRuleCompiler extends RulePredicateCompiler {
        def compile(rule: Rule): RulePredicate = {
            new RegexRulePredicate(rule)
        }
    }
    
    private object IRegexRuleCompiler extends RulePredicateCompiler {
        def compile(rule: Rule): RulePredicate = {
            new IRegexRulePredicate(rule)
        }
    }
    
    private object FileTypeRuleCompiler extends RulePredicateCompiler {
        def compile(rule: Rule): RulePredicate = {
            new FileTypeRulePredicate(rule)
        }
    }
}