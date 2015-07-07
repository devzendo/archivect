package org.devzendo.archivect.rule

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

import org.scalatest.junit.{AssertionsForJUnit, MustMatchersForJUnit}
import org.junit.{Test}

import java.io.File

import org.devzendo.xpfsa.UnixFileStatus._
import org.devzendo.xpfsa.impl.UnixFileStatusImpl

import org.devzendo.archivect.model.Rule
import org.devzendo.archivect.model.CommandModel.RuleType._

class TestFileTypeRuleCompiler extends AssertionsForJUnit with MustMatchersForJUnit {
    val compiler = new RuleCompiler()

    @Test
    def garbageFileTypeIsDetected() {
        compileFailsWithMessage("garbage", "A file type must be a single letter: f, d, l")
    }

    private def compileFailsWithMessage(ruleText: String, message: String) {
        val ex = intercept[IllegalStateException] {
            compiler.compile(Rule(FileType, ruleText, "/tmp"))
        }
        ex.getMessage must equal(message)
    }

    // Tests for UNIX

    @Test
    def unixMatchesFileCorrectlyWithF() {
        val predicate = compiler.compile(Rule(FileType, "f", "/tmp"))
        val cFileStatus = new UnixFileStatusImpl(0L, 0L, S_IFREG, 0, 0, 0, 0L, 0L, 0, 0L, 0, 0, 0)
        val cFile = StubDetailedFile(new File("/tmp/foo.c"), cFileStatus)
        predicate.matches(cFile) must be(true)
    }

    @Test
    def unixMismatchesFileCorrectlyWithD() {
        val predicate = compiler.compile(Rule(FileType, "d", "/tmp"))
        val cFileStatus = new UnixFileStatusImpl(0L, 0L, S_IFREG, 0, 0, 0, 0L, 0L, 0, 0L, 0, 0, 0)
        val cFile = StubDetailedFile(new File("/tmp/foo.c"), cFileStatus)
        predicate.matches(cFile) must be(false)
    }

    @Test
    def unixMismatchesFileCorrectlyWithL() {
        val predicate = compiler.compile(Rule(FileType, "l", "/tmp"))
        val cFileStatus = new UnixFileStatusImpl(0L, 0L, S_IFREG, 0, 0, 0, 0L, 0L, 0, 0L, 0, 0, 0)
        val cFile = StubDetailedFile(new File("/tmp/foo.c"), cFileStatus)
        predicate.matches(cFile) must be(false)
    }

    @Test
    def unixMatchesDirectoryCorrectlyWithD() {
        val predicate = compiler.compile(Rule(FileType, "d", "/tmp"))
        val tmpDirStatus = new UnixFileStatusImpl(0L, 0L, S_IFDIR, 0, 0, 0, 0L, 0L, 0, 0L, 0, 0, 0)
        val tmpDir = StubDetailedFile(new File("/tmp"), tmpDirStatus)
        predicate.matches(tmpDir) must be(true)
    }

    @Test
    def unixMismatchesDirectoryCorrectlyWithF() {
        val predicate = compiler.compile(Rule(FileType, "f", "/tmp"))
        val tmpDirStatus = new UnixFileStatusImpl(0L, 0L, S_IFDIR, 0, 0, 0, 0L, 0L, 0, 0L, 0, 0, 0)
        val tmpDir = StubDetailedFile(new File("/tmp"), tmpDirStatus)
        predicate.matches(tmpDir) must be(false)
    }

    @Test
    def unixMismatchesDirectoryCorrectlyWithL() {
        val predicate = compiler.compile(Rule(FileType, "l", "/tmp"))
        val tmpDirStatus = new UnixFileStatusImpl(0L, 0L, S_IFDIR, 0, 0, 0, 0L, 0L, 0, 0L, 0, 0, 0)
        val tmpDir = StubDetailedFile(new File("/tmp"), tmpDirStatus)
        predicate.matches(tmpDir) must be(false)
    }

    @Test
    def unixMatchesSymlinkCorrectlyWithL() {
        val predicate = compiler.compile(Rule(FileType, "l", "/tmp"))
        val tmpLinkStatus = new UnixFileStatusImpl(0L, 0L, S_IFLNK, 0, 0, 0, 0L, 0L, 0, 0L, 0, 0, 0)
        val tmpLink = StubDetailedFile(new File("/tmp/link"), tmpLinkStatus)
        predicate.matches(tmpLink) must be(true)
    }

    @Test
    def unixMismatchesSymlinkCorrectlyWithF() {
        val predicate = compiler.compile(Rule(FileType, "f", "/tmp"))
        val tmpLinkStatus = new UnixFileStatusImpl(0L, 0L, S_IFLNK, 0, 0, 0, 0L, 0L, 0, 0L, 0, 0, 0)
        val tmpLink = StubDetailedFile(new File("/tmp/link"), tmpLinkStatus)
        predicate.matches(tmpLink) must be(false)
    }

    @Test
    def unixMismatchesSymlinkCorrectlyWithD() {
        val predicate = compiler.compile(Rule(FileType, "d", "/tmp"))
        val tmpLinkStatus = new UnixFileStatusImpl(0L, 0L, S_IFLNK, 0, 0, 0, 0L, 0L, 0, 0L, 0, 0, 0)
        val tmpLink = StubDetailedFile(new File("/tmp/link"), tmpLinkStatus)
        predicate.matches(tmpLink) must be(false)
    }

    // Tests for Windows

    // Tests for Mac OSX
}