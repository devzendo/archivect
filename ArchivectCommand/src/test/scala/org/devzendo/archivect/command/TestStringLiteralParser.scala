package org.devzendo.archivect.command

import scala.util.parsing.combinator._
import org.scalatest.junit.{ AssertionsForJUnit, MustMatchersForJUnit }
import org.junit.{Test, Ignore}

/**
 * How can Java strings containing backslashes (e.g. in regexes, using
 * \. to match a literal dot) be written, so they can be parsed?
 *
 */
@Ignore
class TestStringLiteralParser extends AssertionsForJUnit with MustMatchersForJUnit {
    @Test
    def cannotBeParsedDueToEscapedSlash() {
        new ExampleParser().parseStringLiteral(""""^spaces in name.*\.c$"""") must be ("^spaces in name.*\\.c$")
    }

    @Test
    def comparisonFailsDueToIncorrectlyEscapedSlash() {
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
    def matchingDot() {
        val r = """^\.$""".r
        val s = "."
        val x = r findFirstIn s 
        x must be (Some("."))
    }
    
    class ExampleParser extends JavaTokenParsers {
        private def stringLiteralWithQuotedSlash: Parser[String] = 
            ("\""+"""([^"\p{Cntrl}\\]|\\[\\/bfnrt]|\\|\\u[a-fA-F0-9]{4})*"""+"\"").r
            //   added ---------------------------^^^ this 

        def parseStringLiteral(line: String): String = {
            val parserOutput = parseAll(stringLiteralParser, line)
            parserOutput match {
                case this.Success(r, _) => return r
                case x => throw new IllegalStateException("Could not parse the string '" + line + "': " + x)
            }
        }
        private def stringLiteralParser: Parser[String] = ( 
              stringLiteralWithQuotedSlash ^^ (x => x.substring(1, x.length - 1))
        )
    }
}