/**
 * Copyright (C) 2008-2012 Matt Gumbley, DevZendo.org <http://devzendo.org>
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

package org.devzendo.archivect.sources

import org.scalatest.junit.{MustMatchersForJUnit, AssertionsForJUnit}
import org.junit.Test


class TestFakeDirPredicate extends AssertionsForJUnit with MustMatchersForJUnit {
    @Test
    def notAllElementsAreDirs() {
        dirPredicateIs("""dir\b\c""", false)
        dirPredicateIs("""b\dir\c""", false)
        dirPredicateIs("""b\b\dir""", false)
    }

    @Test
    def allElementsAreDirs() {
        dirPredicateIs("""dir\diva\dog""", true)
        dirPredicateIs("""dir""", true)
    }

    @Test
    def allElementsAreFiles() {
        dirPredicateIs("""file""", false)
        dirPredicateIs("""file\another""", false)
        dirPredicateIs("""some\file\name""", false)
    }

    def dirPredicateIs(path: String,  expected: Boolean) {
        FakeDirPredicate(SourceFactory._pathToSource(path,
            SourceFactory.WINDOWS_SEPARATOR)) must be(expected)
    }
}
