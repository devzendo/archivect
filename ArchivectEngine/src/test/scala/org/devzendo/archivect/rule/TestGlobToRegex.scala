package org.devzendo.archivect.rule

import org.devzendo.archivect.rule.GlobToRegex.globAsRegex
import org.junit.{Test, Ignore}
import org.scalatest.junit.{AssertionsForJUnit, MustMatchersForJUnit}

class TestGlobToRegex extends AssertionsForJUnit with MustMatchersForJUnit {

    @Test
    def noGlobCharactersReturnsWithoutChange() {
        globAsRegex("foo").toString() must equal("^foo$")
    }

    @Test
    def dotIsEscaped() {
        globAsRegex("foo.txt").toString() must equal("""^foo\.txt$""")
    }

    @Test
    def starIsChangedToMatchZeroOrMoreDots() {
        globAsRegex("*txt").toString() must equal("""^.*txt$""")
    }

    @Test
    def questionIsChangedToMatchDot() {
        globAsRegex("foo?txt").toString() must equal("""^foo.txt$""")
    }

    @Test
    def compositeGlob() {
        globAsRegex("xyz*.fo?ba*.r").toString() must equal("""^xyz.*\.fo.ba.*\.r$""")
    }

}