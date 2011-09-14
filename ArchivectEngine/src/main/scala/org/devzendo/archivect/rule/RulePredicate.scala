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
import org.devzendo.xpfsa.{ DetailedFile, FileStatus, UnixFileStatus }
import java.util.regex.{ Pattern, PatternSyntaxException }

sealed abstract class RulePredicate(val rule: Rule) {
    def matches(file: DetailedFile): Boolean
}

case class GlobRulePredicate(override val rule: Rule) extends RulePredicate(rule) {
    val globAsRegex = GlobToRegex.globAsRegex(rule.ruleText).toString()
    def matches(file: DetailedFile): Boolean = {
        val name = file.getFile().getName()
        name.matches(globAsRegex)
    }
}

object RegexRuleHelper {
    def error(message: String, pse: PatternSyntaxException) = {
        throw new IllegalStateException(
            if (pse.getIndex() >= 0)
                message + " (near position " + pse.getIndex() + ")"
            else 
                message
        )
    }
}

case class RegexRulePredicate(override val rule: Rule) extends RulePredicate(rule) {
    val matcher = {
        try {
            Pattern.compile(rule.ruleText).matcher("")
        } catch {
            case pse: PatternSyntaxException =>
                val message = "The regex rule '" + rule.ruleText + 
                    "' is not a valid regex: " + pse.getDescription()
                RegexRuleHelper.error(message, pse)
        }
    }
    def matches(file: DetailedFile): Boolean = {
        val name = file.getFile().getName()
        matcher.reset(name).matches()
    }
}

case class IRegexRulePredicate(override val rule: Rule) extends RulePredicate(rule) {
    val matcher = {
        try {
            Pattern.compile(rule.ruleText, Pattern.CASE_INSENSITIVE).matcher("")
        } catch {
            case pse: PatternSyntaxException =>
                val message = "The case-insensitive regex rule '" + rule.ruleText + 
                    "' is not a valid regex: " + pse.getDescription() 
                RegexRuleHelper.error(message, pse)
        }
    }
    def matches(file: DetailedFile): Boolean = {
        val name = file.getFile().getName()
        matcher.reset(name).matches()
    }
}

case class FileTypeRulePredicate(override val rule: Rule) extends RulePredicate(rule) {
    val fileTypeString = rule.ruleText.trim().toLowerCase()
    if (!fileTypeString.matches("^[fdl]$")) {
        throw new IllegalStateException("A file type must be a single letter: f, d, l")
    }
    
    def matches(file: DetailedFile): Boolean = {
        val fs = file.getFileStatus()
        fileTypeString match {
            case "f" => {
                fs match {
                    case ufs: UnixFileStatus =>
                        ufs.isRegularFile()
                }
            }
            case "d" => {
                fs match {
                    case ufs: UnixFileStatus =>
                        ufs.isDirectory()
                }
            }
            case "l" => {
                fs match {
                    case ufs: UnixFileStatus =>
                        ufs.isSymbolicLink()
                }
            }
        }
    }
}
