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
package org.devzendo.archivect.sources

import org.scalatest.junit.{ AssertionsForJUnit, MustMatchersForJUnit }
import org.junit.{ Test }
import org.devzendo.archivect.sources.Sources._

// Slashes in strings....
// """\\server\share""" is a good UNC path
// "\\\\server\\share" is good also

class TestSources extends AssertionsForJUnit with MustMatchersForJUnit {
    @Test
    def unrootedPathGivesUnrootedSource() {
        val source = Sources.pathToSource("a/b/c")
        source match {
            case unrootedSource: UnrootedSource =>
                unrootedSource.path must be("a/b/c")
                unrootedSource.pathComponents must be(List("a", "b", "c"))
            case _ => fail("did not return an UnrootedSource")
        }
    }

    @Test
    def uncPathWithoutSubcomponentsPathGivesUNCSource() {
        val source = Sources.pathToSource("""\\server\share""")
        source match {
            case uncSource: UNCSource =>
                uncSource.server must be("server")
                uncSource.share must be("share")
                uncSource.path must be("")
                uncSource.pathComponents.size must equal(0)
            case x => fail("did not return a UNCSource; got a " + x.getClass.getName + ": '" + x + "'")
        }
    }

    @Test
    def uncPathWithEmptySubcomponentsPathGivesUNCSource() {
        val source = Sources.pathToSource("""\\server\share\""")
        source match {
            case uncSource: UNCSource =>
                uncSource.server must be("server")
                uncSource.share must be("share")
                uncSource.path must be("")
                uncSource.pathComponents.size must equal(0)
            case x => fail("did not return a UNCSource; got a " + x.getClass.getName + ": '" + x + "'")
        }
    }

    @Test
    def uncPathWithNonEmptySubcomponentsPathGivesUNCSource() {
        val source = Sources.pathToSource("""\\server\share\foo\bar\quux""")
        source match {
            case uncSource: UNCSource =>
                uncSource.server must be("server")
                uncSource.share must be("share")
                uncSource.path must be("foo\\bar\\quux")
                uncSource.pathComponents must be (List("foo", "bar", "quux"))
            case x => fail("did not return a UNCSource; got a " + x.getClass.getName + ": '" + x + "'")
        }
    }

    @Test
    def drivePathWithoutSubcomponentsPathGivesWindowsDriveSource() {
        val source = Sources.pathToSource("""d:""")
        source match {
            case driveSource: WindowsDriveSource =>
                driveSource.driveLetter must be("D:")
                driveSource.path must be("")
                driveSource.pathComponents.size must equal(0)
            case x => fail("did not return a WindowsDriveSource; got a " + x.getClass.getName + ": '" + x + "'")
        }
    }

    @Test
    def drivePathWithSubcomponentsPathGivesWindowsDriveSource() {
        val source = Sources.pathToSource("""d:test.foo""")
        source match {
            case driveSource: WindowsDriveSource =>
                driveSource.driveLetter must be("D:")
                driveSource.path must be("test.foo")
                driveSource.pathComponents must be (List("test.foo"))
            case x => fail("did not return a WindowsDriveSource; got a " + x.getClass.getName + ": '" + x + "'")
        }
    }

    @Test
    def drivePathWithAbsoluteSubcomponentsPathGivesWindowsDriveSource() {
        val source = Sources.pathToSource("""d:\path\to\test.foo""")
        source match {
            case driveSource: WindowsDriveSource =>
                driveSource.driveLetter must be("D:")
                driveSource.path must be("path\\to\\test.foo")
                driveSource.pathComponents must be (List("path", "to", "test.foo"))
            case x => fail("did not return a WindowsDriveSource; got a " + x.getClass.getName + ": '" + x + "'")
        }
    }
}