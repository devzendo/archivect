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
          stringLiteral ^^ (x => x.substring(1, x.length - 1))
        | word ^^ (x => x)
    )
    private def word: Parser[String] = """\S+""".r // simplistic, compared with stringLiteral
    private def failWith(msg: String): Parser[String] = (
          word ^^ (x => throw new IllegalStateException(msg + " '" + x + "'"))
    )
    def parseLine(line: String) = {
        parseAll(ruleLineParser, line)
    }
}
