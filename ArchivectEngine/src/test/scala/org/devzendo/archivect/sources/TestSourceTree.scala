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
    val sourceTree = new UnrootedSourceTree(FakeDirPredicate)
    val rootNode = sourceTree.getRootNode

    @Test
    @Ignore
    def sourceTreeHasPopulatedDirectoryNodes() {
        val sources = new SourcesRegistry(FakeDirPredicate)
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
    def dirNodeDirAdditionWorksAsExpected() {
        val dn = new DirNode("")
        dn.getDirNodes.contains("foo") must be (false)

        val foo = dn.addDir("foo")
        dn.getDirNodes.contains("foo") must be (true)

        val bar = dn.addDir("bar")
        dn.getDirNodes.contains("bar") must be (true)
        dn.getDirNodes.contains("foo") must be (true) // still

        foo must not (be theSameInstanceAs bar)

        val foo2 = dn.addDir("foo")
        dn.getDirNodes.contains("foo") must be (true)

        foo2 must (be theSameInstanceAs foo)
    }

    @Test
    def rootSourceCanBeAddedAndFound() {
        // you can always get the root node
        sourceTree.findNode("/").isDefined must be(true)
        sourceTree.findNode("/").get.sourcePathTermination must be(false)

        // but it is only terminated when it is added
        _addSource("/")

        sourceTree.findNode("/").get.sourcePathTermination must be(true)
    }

    @Test
    def directorySourceCanBeAddedAndFound() {
        _addSource("/a/b/c")
        sourceTree.findNode("/a").isDefined must be(true)
        sourceTree.findNode("/a/b").isDefined must be(true)
        sourceTree.findNode("/a/b/c").get.sourcePathTermination must be(true)
    }

    @Test
    def directoryThatDoesNotExistCannotBeFound() {
        sourceTree.findNode("/a").isEmpty must be(true)
        sourceTree.findNode("/a/b").isEmpty must be(true)
        sourceTree.findNode("/a/b/c").isEmpty must be(true)
    }

    def _addSource(path: String) {
        sourceTree.addSource(
            SourceFactory._pathToSource(path, SourceFactory.UNIX_SEPARATOR))
    }

    @Test
    def addRuleAtRootSourcePathAllowed() {
        _addSource("/")
        sourceTree.getRulesAtDir("/").size must be(0)

        val rule = compiler.compile(Rule(Glob, "*.c", "/"))
        sourceTree.addIncludeRule(rule)

        // TODO unfinished
        val rules = sourceTree.getRulesAtDir("/")
        rules.size must be(1)
        rules(0) must be theSameInstanceAs rule
    }

    @Test
    def addRuleAtSourcePathAllowed() {
        _addSource("dtmp")
        sourceTree.getRulesAtDir("/dtmp").size must be(0)

        val rule = compiler.compile(Rule(Glob, "*.c", "/dtmp"))
        sourceTree.addIncludeRule(rule)

        // TODO unfinished
        val rules = sourceTree.getRulesAtDir("/dtmp")
        rules.size must be(1)
        rules(0) must be theSameInstanceAs rule
    }

    @Test
    def addRuleAtDeeperSourcePathAllowed() {
        _addSource("dtmp/done")
        sourceTree.getRulesAtDir("/dtmp/done").size must be(0)

        val rule = compiler.compile(Rule(Glob, "*.c", "/dtmp/done"))
        sourceTree.addIncludeRule(rule)

        // TODO unfinished
        val rules = sourceTree.getRulesAtDir("/dtmp/done")
        rules.size must be(1)
        rules(0) must be theSameInstanceAs rule
    }

    @Test
    def addRuleUnderSourceDirAllowed() {
        _addSource("dtmp")
        sourceTree.getRulesAtDir("/dtmp").size must be(0)

        val rule = compiler.compile(Rule(Glob, "*.c", "/dtmp/dunder/directory"))
        sourceTree.addIncludeRule(rule)

        // TODO unfinished
        sourceTree.getRulesAtDir("/dtmp").size must be(0)

        sourceTree.getRulesAtDir("/dtmp/dunder").size must be(0)

        val endRules = sourceTree.getRulesAtDir("/dtmp/dunder/directory")
        endRules.size must be(1)
        endRules(0) must be theSameInstanceAs rule
    }

    @Test
    def addRuleAwayFromSourceDirNotAllowed() {
        _addSource("dtmp")
        sourceTree.getRulesAtDir("/dtmp").size must be(0)

        intercept[SourceTreeException] (
            sourceTree.addIncludeRule(
                compiler.compile(Rule(Glob, "*.c", "/dway/daway")))
        ).getMessage must
            be("Cannot add rule '*.c' at '/dway/daway': rules can only be added at, or under source paths")
    }

    @Test
    def addRuleAboveSourceDirNotAllowed() {
        _addSource("dtmp/dunder")
        sourceTree.getRulesAtDir("/dtmp").size must be(0)
        sourceTree.getRulesAtDir("/dtmp/dunder").size must be(0)

        intercept[SourceTreeException] (
            sourceTree.addIncludeRule(
                compiler.compile(Rule(Glob, "*.c", "/dtmp")))
        ).getMessage must
            be("Cannot add rule '*.c' at '/dtmp': rules can only be added at, or under source paths")
    }

    @Test
    def allPathComponentsMustBeDirectories() {
        _addSource("dtmp")

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
