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
import org.scalatest.junit.{ AssertionsForJUnit, MustMatchersForJUnit }
import org.junit.{Test, Ignore}

/**
 * How can Java strings containing backslashes (e.g. in regexes, using
 * \. to match a literal dot) be written, so they can be parsed?
 *
 */
class TestStringLiteralParser extends AssertionsForJUnit with MustMatchersForJUnit {
    @Test
    def correctEscapingCannotBeParsedByLibraryStringLiterlParserDueToEscapedSlash() {
        // but parses correctly using my version
        new ExampleParser().parseStringLiteral(""""^spaces in name.*\.c$"""") must be ("^spaces in name.*\\.c$")
    }

    @Test
    def multipleSlashesAreFine() {
        new ExampleParser().parseStringLiteral(""""^spaces in name.*\\.c$"""") must be ("^spaces in name.*\\\\.c$")
    }

    @Test
    def thisParsesFineWhenThereAreNoEscapes() {
        new ExampleParser().parseStringLiteral(""""^spaces in name.*.c$"""") must be ("^spaces in name.*.c$")
    }

    @Test
    def thisParsesFineWhenThereIsAnEscapedNewline() {
        new ExampleParser().parseStringLiteral(""""^spaces in name.*\nc$"""") must be ("^spaces in name.*\\nc$")
    }

    @Test
    def thisParsesFineWhenThereIsAnEscapedBackspace() {
        new ExampleParser().parseStringLiteral(""""^spaces in name.*\bc$"""") must be ("^spaces in name.*\\bc$")
    }

    @Test
    def whatIsTheCorrectRegexForMatchingDotAsARawString() {
        val r = """^\.$""".r
        val dotString = "."
        val x = r findFirstIn dotString // regex . would match text ., hence the nonDotString test
        x must be (Some("."))
        val nonDotString = "x"
        val y = r findFirstIn nonDotString // the regex . would not give None here
        y must be (None)
    }
    
    class ExampleParser extends JavaTokenParsers with CustomRegexParsers {
        def parseStringLiteral(line: String): String = {
            val parserOutput = parseAll(stringLiteralParser, line)
            parserOutput match {
                case this.Success(r, _) => return r
                case x => throw new IllegalStateException("Could not parse the string '" + line + "': " + x)
            }
        }
        private def stringLiteralParser: Parser[String] = ( 
            stringLiteralWithQuotedSlash ^^ (x => x.substring(1, x.length - 1))
            // Standard library stringLiteral parser fails to parse slashes
            //  stringLiteral ^^ (x => x.substring(1, x.length - 1))
        )
    }
}