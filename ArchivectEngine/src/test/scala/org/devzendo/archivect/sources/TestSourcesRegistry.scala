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
import org.junit.Test
import org.devzendo.archivect.sources.SourceTreeFactory._

class TestSourcesRegistry extends AssertionsForJUnit with MustMatchersForJUnit {
    val sources = new SourcesRegistry()

    @Test
    def newSourcesHaveNoSources() {
        sources.getSources.size must be(0)
    }

    @Test
    def unrootedSourcesAreStoredInSameSourceTree() {
        val sourceTree1 = sources.addSource(SourceFactory._pathToSource("""a\b\c""",
            SourceFactory.UNIX_SEPARATOR))
        val sourceTree2 = sources.addSource(SourceFactory._pathToSource("""r\s\t""",
            SourceFactory.UNIX_SEPARATOR))
        sourceTree1 must be theSameInstanceAs sourceTree2
        sourceTree1 match {
            case unrootedSourceTree: UnrootedSourceTree =>
            case x => fail("did not return a UnrootedSourceTree; got a " +
                x.getClass.getName + ": '" + x + "'")
        }
    }

    @Test
    def rootedSourcesAreStoredInSameSourceTree() {
        val sourceTree1 = sources.addSource(SourceFactory._pathToSource("""\a\b\c""",
            SourceFactory.UNIX_SEPARATOR))
        val sourceTree2 = sources.addSource(SourceFactory._pathToSource("""\r\s\t""",
            SourceFactory.UNIX_SEPARATOR))
        sourceTree1 must be theSameInstanceAs sourceTree2
        sourceTree1 match {
            case rootedSourceTree: RootedSourceTree =>
            case x => fail("did not return a RootedSourceTree; got a " +
                x.getClass.getName + ": '" + x + "'")
        }
    }

    @Test
    def windowsDriveSourcesWithSameDriveAreStoredInSameSourceTree() {
        val sourceTree1 = sources.addSource(SourceFactory._pathToSource("""D:\a\b\c""",
            SourceFactory.WINDOWS_SEPARATOR))
        val sourceTree2 = sources.addSource(SourceFactory._pathToSource("""D:\r\s\t""",
            SourceFactory.WINDOWS_SEPARATOR))
        sourceTree1 must be theSameInstanceAs sourceTree2
        sourceTree1 match {
            case windowsDriveSourceTree: WindowsDriveSourceTree =>
            case x => fail("did not return a WindowsDriveSourceTree; got a " +
                x.getClass.getName + ": '" + x + "'")
        }
    }

    @Test
    def windowsDriveSourcesWithDifferentDriveAreStoredInSameDifferentTree() {
        val sourceTree1 = sources.addSource(SourceFactory._pathToSource("""D:\a\b\c""",
            SourceFactory.WINDOWS_SEPARATOR))
        val sourceTree2 = sources.addSource(SourceFactory._pathToSource("""C:\r\s\t""",
            SourceFactory.WINDOWS_SEPARATOR))
        sourceTree1 must not (be theSameInstanceAs sourceTree2)
        sourceTree1 match {
            case windowsDriveSourceTree: WindowsDriveSourceTree =>
            case x => fail("did not return a WindowsDriveSourceTree; got a " +
                x.getClass.getName + ": '" + x + "'")
        }
    }

    @Test
    def uncSourcesWithSameRootAreStoredInSameSourceTree() {
        val sourceTree1 = sources.addSource(SourceFactory._pathToSource("""\\server\share\a\b\c""",
            SourceFactory.WINDOWS_SEPARATOR))
        val sourceTree2 = sources.addSource(SourceFactory._pathToSource("""\\server\share\r\s\t""",
            SourceFactory.WINDOWS_SEPARATOR))
        sourceTree1 must be theSameInstanceAs sourceTree2
        sourceTree1 match {
            case uncSourceTree: UNCSourceTree =>
            case x => fail("did not return a UNCSourceTree; got a " +
                x.getClass.getName + ": '" + x + "'")
        }
    }

    @Test
    def uncSourcesWithDifferentRootAreStoredInSameDifferentTree() {
        val sourceTree1 = sources.addSource(SourceFactory._pathToSource("""\\server1\share\a\b\c""",
            SourceFactory.WINDOWS_SEPARATOR))
        val sourceTree2 = sources.addSource(SourceFactory._pathToSource("""\\server2\share\r\s\t""",
            SourceFactory.WINDOWS_SEPARATOR))
        sourceTree1 must not (be theSameInstanceAs sourceTree2)
        sourceTree1 match {
            case uncSourceTree: UNCSourceTree =>
            case x => fail("did not return a UNCSourceTree; got a " +
                x.getClass.getName + ": '" + x + "'")
        }
    }
}
