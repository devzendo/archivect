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
    def removeLeadingSlashes() {
        Sources._removeLeadingSlashes("n/o slashes at start") must be("n/o slashes at start")
        Sources._removeLeadingSlashes("\\\\windows \\ slashes") must be("windows \\ slashes")
        Sources._removeLeadingSlashes("/////unix / slashes") must be("unix / slashes")
    }

    @Test
    def slashConversion() {
        Sources._convertSlashes("""/one/two/three/""", Sources.UNIX_SEPARATOR) must be ("""/one/two/three/""")
        Sources._convertSlashes("""/one/two/three/""", Sources.WINDOWS_SEPARATOR) must be ("""\one\two\three\""")
        Sources._convertSlashes("""\one\two\three\""", Sources.UNIX_SEPARATOR) must be ("""/one/two/three/""")
        Sources._convertSlashes("""\one\two\three\""", Sources.WINDOWS_SEPARATOR) must be ("""\one\two\three\""")
    }

    @Test
    def wrongSlashesInUnixUnrootedPathAreCorrected() {
        val source = Sources._pathToSource("""a\b\c""", Sources.UNIX_SEPARATOR)
        source match {
            case unrootedSource: UnrootedSource =>
                unrootedSource.path must be("""a/b/c""")
            case _ => fail("did not return an UnrootedSource")
        }
    }

    @Test
    def wrongSlashesInWindowsUnrootedPathAreCorrected() {
        val source = Sources._pathToSource("""a/b/c""", Sources.WINDOWS_SEPARATOR)
        source match {
            case unrootedSource: UnrootedSource =>
                unrootedSource.path must be("""a\b\c""")
            case _ => fail("did not return an UnrootedSource")
        }
    }

    @Test
    def rightSlashesInUnixUnrootedPathAreNotChanged() {
        val source = Sources._pathToSource("""a/b/c""", Sources.UNIX_SEPARATOR)
        source match {
            case unrootedSource: UnrootedSource =>
                unrootedSource.path must be("""a/b/c""")
            case _ => fail("did not return an UnrootedSource")
        }
    }

    @Test
    def rightSlashesInWindowsUnrootedPathAreNotChanged() {
        val source = Sources._pathToSource("""a\b\c""", Sources.WINDOWS_SEPARATOR)
        source match {
            case unrootedSource: UnrootedSource =>
                unrootedSource.path must be("""a\b\c""")
            case _ => fail("did not return an UnrootedSource")
        }
    }

    @Test
    def uncPathWithWrongSlashesAndNonEmptySubcomponentsPathGivesUNCSource() {
        val source = Sources.pathToSource("""//server/share/foo/bar/quux""")
        source match {
            case uncSource: UNCSource =>
                uncSource.server must be("server")
                uncSource.share must be("share")
                uncSource.path must be("foo\\bar\\quux")
                uncSource.pathComponents must be (List("foo", "bar", "quux"))
                uncSource.root must be("""\\server\share""")
            case x => fail("did not return a UNCSource; got a " + x.getClass.getName + ": '" + x + "'")
        }
    }

    @Test
    def drivePathWithWrongSlashesAndAbsoluteSubcomponentsPathGivesWindowsDriveSource() {
        val source = Sources.pathToSource("""d:/path/to/test.foo""")
        source match {
            case driveSource: WindowsDriveSource =>
                driveSource.driveLetter must be("D:")
                driveSource.path must be("path\\to\\test.foo")
                driveSource.pathComponents must be (List("path", "to", "test.foo"))
                driveSource.root must be("D:")
            case x => fail("did not return a WindowsDriveSource; got a " + x.getClass.getName + ": '" + x + "'")
        }
    }

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
                uncSource.root must be("""\\server\share""")
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
                uncSource.root must be("""\\server\share""")
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
                uncSource.root must be("""\\server\share""")
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
                driveSource.root must be("D:")
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
                driveSource.root must be("D:")
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
                driveSource.root must be("D:")
            case x => fail("did not return a WindowsDriveSource; got a " + x.getClass.getName + ": '" + x + "'")
        }
    }
}