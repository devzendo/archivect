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
import org.scalatest.junit.{ AssertionsForJUnit, MustMatchersForJUnit }
import org.junit.{ Test }

class TestPathOrdering extends AssertionsForJUnit with MustMatchersForJUnit {

    @Test
    def zeroComesFirst() {
        lt("", "a")
        gt("a", "")
    }

    @Test
    def heightComesFirst() {
        lt("a", "a/b")
        gt("a/b", "a")
    }

    @Test
    def lessThanLexicographic() {
        lt("a/b", "a/c")
        gt("a/c", "a/b")
    }

    @Test
    def lexicographicFirst() {
        lt("a/c", "z")
        gt("z", "a/c")
    }

    @Test
    def equality() {
        equiv("a/c", "a/c")
    }

    def equiv(s1: String,  s2: String) {
        SourcesRegistry.pathOrdering.equiv(
            SourceFactory._pathToSource(s1, SourceFactory.UNIX_SEPARATOR),
            SourceFactory._pathToSource(s2, SourceFactory.UNIX_SEPARATOR)
        ) must be (true)
    }
    def lt(s1: String,  s2: String) {
        SourcesRegistry.pathOrdering.lt(
            SourceFactory._pathToSource(s1, SourceFactory.UNIX_SEPARATOR),
            SourceFactory._pathToSource(s2, SourceFactory.UNIX_SEPARATOR)
        ) must be (true)
    }
    def gt(s1: String,  s2: String) {
        SourcesRegistry.pathOrdering.gt(
            SourceFactory._pathToSource(s1, SourceFactory.UNIX_SEPARATOR),
            SourceFactory._pathToSource(s2, SourceFactory.UNIX_SEPARATOR)
        ) must be (true)
    }
}
