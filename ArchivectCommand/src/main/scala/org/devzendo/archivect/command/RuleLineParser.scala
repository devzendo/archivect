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

import scala.util.parsing.combinator._

import org.devzendo.archivect.command.CommandModel.RuleType._

/**
 * A parser of lines in a rule inclusion/exclusion file.
 * These files can contain comments in any position, starting with # comment
 * Blank lines are allowed, and rules are of the form:
 * +|- <ruleType> <ruleText> <ruleAtDirectory>
 * + denotes that the rule will accept files at this directory.
 * - denotes that the rule will exclude files at this directory.
 * 
 * @author matt
 *
 */
class RuleLineParser extends JavaTokenParsers {
    private def ruleLineParser: Parser[Option[Tuple2[Boolean, Rule]]] = (
          comment ^^ (x => None)
        | opt(ruleStatement) ~ opt(comment) ^^ {
            case ruleStmt ~ comment =>
                if (ruleStmt.isDefined) {
                    Some(ruleStmt.get)
                } else {
                    None
                }
          }
    )
    private def comment: Parser[String] = """#.*""".r
    private def ruleStatement: Parser[Tuple2[Boolean, Rule]] = inclusionExclusion ~ ruleType ~ possiblyQuotedWord ~ possiblyQuotedWord ^^ {
        case isInclusion ~ ruleTypeEnum ~ ruleTextText ~ ruleAtText =>
            (isInclusion, new Rule(ruleTypeEnum, ruleTextText, ruleAtText))
    }
    private def inclusionExclusion: Parser[Boolean] = (
          "+" ^^ (x => true)
        | "-" ^^ (x => false)
        | failWith("Unknown rule inclusion/exclusion type") ^^ (x => true) // NOTE: throws instead
    )
    private def ruleType: Parser[RuleType] = (
          "glob" ^^ (x => Glob)
        | "regex" ^^ (x => Regex)
        | "iregex" ^^ (x => IRegex)
        | "type" ^^ (x => FileType)
        | "filetype" ^^ (x => FileType)
        | failWith("Unknown rule type") ^^ (x => Glob) // NOTE: throws instead
    )
    private def possiblyQuotedWord: Parser[String] = ( 
          stringLiteralWithQuotedSlash ^^ (x => x.substring(1, x.length - 1))
        | word ^^ (x => x)
    )
    // Same as stringLiteral in the base class, but can handle \\ in quoted
    // strings.
    private def stringLiteralWithQuotedSlash: Parser[String] = 
        ("\""+"""([^"\p{Cntrl}\\]|\\[\\/bfnrt]|\\u[a-fA-F0-9]{4})*"""+"\"").r
    private def word: Parser[String] = """\S+""".r // simplistic, compared with stringLiteral
    private def failWith(msg: String): Parser[String] = (
          word ^^ (x => throw new IllegalStateException(msg + " '" + x + "'"))
    )

    /**
     * @param line a line of text from a rules file, can be blank, can contain
     * comments starting with #
     * @return Some inclusion/exclusion state and rule if this line
     * contains a rule, and None if this line is blank or contains just a
     * comment. 
     */
    def parseLine(line: String): Option[Tuple2[Boolean, Rule]] = {
        val parserOutput = parseAll(ruleLineParser, line)
        parserOutput match {
            case this.Success(r, _) => return r
            case x => throw new IllegalStateException("Could not parse the rule line '" + line + "'")
        }
    }
}
