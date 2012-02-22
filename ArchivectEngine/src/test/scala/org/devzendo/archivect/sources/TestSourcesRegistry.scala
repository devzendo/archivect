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

import scala.util.Random
import org.scalatest.junit.{ AssertionsForJUnit, MustMatchersForJUnit }
import org.junit.Test
import org.devzendo.archivect.sources.SourceTreeFactory._

class TestSourcesRegistry extends AssertionsForJUnit with MustMatchersForJUnit {
    val sources = new SourcesRegistry()

    @Test
    def newSourcesHaveNoSources() {
        sources.getSourceTrees.size must be(0)
    }

    @Test
    def sourcesAreReturnedInOrder() {
        sources.addSource(SourceFactory._pathToSource
            ("""\\zenserver\emptiness\0""", SourceFactory.WINDOWS_SEPARATOR))
        sources.addSource(SourceFactory._pathToSource("""a\b\c""",
            SourceFactory.UNIX_SEPARATOR))
        sources.addSource(SourceFactory._pathToSource("""a\c""",
            SourceFactory.UNIX_SEPARATOR))
        sources.addSource(SourceFactory._pathToSource("""\z""",
            SourceFactory.UNIX_SEPARATOR))
        sources.addSource(SourceFactory._pathToSource("""\a\b\c""",
            SourceFactory.UNIX_SEPARATOR))
        sources.addSource(SourceFactory._pathToSource("""\a""",
            SourceFactory.UNIX_SEPARATOR))
        sources.addSource(SourceFactory._pathToSource("""Z:\a\c""",
            SourceFactory.WINDOWS_SEPARATOR))
        sources.addSource(SourceFactory._pathToSource("""D:\a\b\c""",
            SourceFactory.WINDOWS_SEPARATOR))
        sources.addSource(SourceFactory._pathToSource("""D:\z""",
            SourceFactory.WINDOWS_SEPARATOR))
        sources.addSource(SourceFactory._pathToSource("""D:\a""",
            SourceFactory.WINDOWS_SEPARATOR))
        sources.addSource(SourceFactory._pathToSource
            ("""\\abcserver\share\a\b\c""", SourceFactory.WINDOWS_SEPARATOR))
        sources.addSource(SourceFactory._pathToSource
            ("""\\abcserver\aardvark\a\b\c""", SourceFactory.WINDOWS_SEPARATOR))

        val src = sources.getSourceTrees
        src.size must be(7)
        expectTree(src(0), classOf[UnrootedSourceTree])
        expectTree(src(1), classOf[RootedSourceTree])
        expectTree(src(2), classOf[WindowsDriveSourceTree], "D:")
        expectTree(src(3), classOf[WindowsDriveSourceTree], "Z:")
        expectTree(src(4), classOf[UNCSourceTree], """\\abcserver\aardvark""")
        expectTree(src(5), classOf[UNCSourceTree], """\\abcserver\share""")
        expectTree(src(6), classOf[UNCSourceTree], """\\zenserver\emptiness""")

        /*
        expectSource(src(0), """a\c""")
        expectSource(src(1), """a\b\c""")
        expectSource(src(2), """\a""")
        expectSource(src(3), """\a\b\c""")
        expectSource(src(4), """\z""")
        expectSource(src(5), "D:", """\a""")
        expectSource(src(6), "D:", """\a\b\c""")
        expectSource(src(7), "D:", """\z""")
        expectSource(src(8), "Z:", """\a\c""")
        expectSource(src(9), """\\abcserver\aardvark""", """\a\b\c""")
        expectSource(src(10), """\\abcserver\share""", """\a\b\c""")
        expectSource(src(11), """\\zenserver\emptiness""", """\0""")
        */
    }

    def expectTree(tree: SourceTree, clazz: Class[_ <: SourceTree]) {
        tree.getClass must be(clazz)
    }

    def expectTree(tree: SourceTree, clazz: Class[_ <: RootedSourceTree],
                   root: String) {
        tree.getClass must be(clazz)
        tree match {
            case rootedSourceTree: RootedSourceTree =>
                rootedSourceTree.root must be(root)
            case x => fail("did not return a RootedSourceTree; got a " +
                x.getClass.getName + ": '" + x + "'")
        }
    }

    @Test
    def windowsDriveSourcesAreReturnedInDriveOrder() {
        val drives = ('A' to 'Z').toList
        for (drive <- Random.shuffle(drives)) {
            sources.addSource(SourceFactory._pathToSource("" + drive + """:\a""",
                SourceFactory.WINDOWS_SEPARATOR))
        }
        val sourceTrees = sources.getSourceTrees
        sourceTrees.size must be(26)
        val driveCharZSourceTrees = drives zip sourceTrees

        for (driveCharZSourceTree <- driveCharZSourceTrees) {
            driveCharZSourceTree._2 match {
                case windowsDriveSourceTree: WindowsDriveSourceTree =>
                    windowsDriveSourceTree.driveLetter must be(driveCharZSourceTree._1 + ":")
                case x => fail("did not return a WindowsDriveSourceTree; got a " +
                    x.getClass.getName + ": '" + x + "'")
            }
        }
    }

    @Test
    def uncSourcesAreReturnedInServerThenShareOrder() {
        addSource("""\\xyz\xyz\a""")
        addSource("""\\xyz\rst\b""")
        addSource("""\\xyz\abc\c""")
        addSource("""\\rst\xyz\d""")
        addSource("""\\rst\rst\e""")
        addSource("""\\rst\abc\f""")
        addSource("""\\abc\xyz\g""")
        addSource("""\\abc\rst\h""")
        addSource("""\\abc\abc\i""")

        val sourceTrees = sources.getSourceTrees
        sourceTrees.size must be(9)
        expectUnc(sourceTrees(0), """\\abc\abc""")
        expectUnc(sourceTrees(1), """\\abc\rst""")
        expectUnc(sourceTrees(2), """\\abc\xyz""")
        expectUnc(sourceTrees(3), """\\rst\abc""")
        expectUnc(sourceTrees(4), """\\rst\rst""")
        expectUnc(sourceTrees(5), """\\rst\xyz""")
        expectUnc(sourceTrees(6), """\\xyz\abc""")
        expectUnc(sourceTrees(7), """\\xyz\rst""")
        expectUnc(sourceTrees(8), """\\xyz\xyz""")
    }

    def addSource(source: String) {
        sources.addSource(SourceFactory._pathToSource(source, SourceFactory.WINDOWS_SEPARATOR))
    }

    def expectUnc(tree: SourceTree, root: String) {
        tree match {
            case uncSourceTree: UNCSourceTree =>
                uncSourceTree.root must be(root)
            case x => fail("did not return a UNCSourceTree; got a " +
                x.getClass.getName + ": '" + x + "'")
        }
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
            case x => fail("did not return an UnrootedSourceTree; got a " +
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
