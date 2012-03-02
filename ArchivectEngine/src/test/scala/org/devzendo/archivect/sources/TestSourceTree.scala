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

import org.scalatest.junit.{AssertionsForJUnit, MustMatchersForJUnit}
import org.devzendo.archivect.sources.SourceTreeFactory._
import org.devzendo.archivect.model.Rule
import org.devzendo.archivect.model.CommandModel.RuleType._

import org.junit.{Ignore, Test}
import org.devzendo.archivect.rule.RuleCompiler

class TestSourceTree extends AssertionsForJUnit with MustMatchersForJUnit {
    val compiler = new RuleCompiler()
    val sources = new SourcesRegistry(FakeDirPredicate)
    val sourceTree = new UnrootedSourceTree(FakeDirPredicate)
    val rootNode = sourceTree.getRootNode

    @Test
    @Ignore
    def sourceTreeHasPopulatedDirectoryNodes() {
        // TODO this test is unfinished
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

//        val src = sources.getSourceTrees

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

    @Test
    def sourceTreeAddSameDirAddsToSameDirNode() {
        val dn1 = rootNode.addDir("a")
        val dn2 = rootNode.addDir("a")
        dn1 must be theSameInstanceAs dn2
    }

    @Test
    def sourceTreeAddDifferentDirAddsToDifferentDirNode() {
        val dn1 = rootNode.addDir("a")
        val dn2 = rootNode.addDir("b")
        dn1 must not (be theSameInstanceAs dn2)
    }

    @Test
    @Ignore
    def sourceTreeAddRuleAtDir() {
        rootNode.addDir("dtmp")
        sourceTree.getRulesAtDir("/dtmp").size must be(0)

        val rule = compiler.compile(Rule(Glob, "*.c", "/dtmp"))
        sourceTree.addIncludeRule(rule)

        // TODO unfinished
        val rules = sourceTree.getRulesAtDir("/dtmp")
        rules.size must be(1)
        rules(0) must be theSameInstanceAs rule
    }

    @Test
    def allPathComponentsMustBeDirectories() {
        rootNode.addDir("dtmp")

        intercept[SourceTreeException] (
            sourceTree.addIncludeRule(
                compiler.compile(Rule(Glob, "*.c", "/dtmp/fnotsubdir/file")))
        ).getMessage must
            be("Cannot add rule '*.c' at '/dtmp/fnotsubdir/file': rules can only be added to directories")

        intercept[SourceTreeException] (
            sourceTree.addIncludeRule(
                compiler.compile(Rule(Glob, "*.txt", "/fnotdir/file")))
        ).getMessage must
            be("Cannot add rule '*.txt' at '/fnotdir/file': rules can only be added to directories")

        intercept[SourceTreeException] (
            sourceTree.addIncludeRule(
                compiler.compile(Rule(Glob, "*.txt", "/dir/file")))
        ).getMessage must
            be("Cannot add rule '*.txt' at '/dir/file': rules can only be added to directories")
    }
}
