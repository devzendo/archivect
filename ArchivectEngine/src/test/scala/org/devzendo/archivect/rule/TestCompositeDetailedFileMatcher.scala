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
package org.devzendo.archivect.rule

import org.scalatest.junit.{AssertionsForJUnit, MustMatchersForJUnit}
import org.junit.{Test}
import org.devzendo.xpfsa.{ DetailedFile, UnixFileStatus }
import org.easymock.EasyMock
import org.easymock.EasyMock._

class TestCompositeDetailedFileMatcher extends AssertionsForJUnit with MustMatchersForJUnit {
    @Test
    def passMatcherPasses() {
        val file1 = EasyMock.createMock(classOf[DetailedFile])
        EasyMock.replay(file1)
        val m = new PassDetailedFileMatcher()
        m.matches(file1) must be(true)
        EasyMock.verify(file1)
    }

    @Test
    def failMatcherFails() {
        val file1 = EasyMock.createMock(classOf[DetailedFile])
        EasyMock.replay(file1)
        val m = new FailDetailedFileMatcher()
        m.matches(file1) must be(false)
        EasyMock.verify(file1)
    }

    @Test
    def notMatcherNegates() {
        val file1 = EasyMock.createMock(classOf[DetailedFile])
        EasyMock.replay(file1)

        val pass = new PassDetailedFileMatcher()
        val notPass = new NotDetailedFileMatcher(pass)
        notPass.matches(file1) must be(false)

        val fail = new FailDetailedFileMatcher()
        val notFail = new NotDetailedFileMatcher(fail)
        notFail.matches(file1) must be(true)

        EasyMock.verify(file1)
    }

    @Test
    def orMatcherOrs() {
        val file1 = EasyMock.createMock(classOf[DetailedFile])
        EasyMock.replay(file1)

        val pass = new PassDetailedFileMatcher()
        val fail = new FailDetailedFileMatcher()

        new OrDetailedFileMatcher(fail, fail).matches(file1) must be(false)
        new OrDetailedFileMatcher(fail, pass).matches(file1) must be(true)
        new OrDetailedFileMatcher(pass, fail).matches(file1) must be(true)
        new OrDetailedFileMatcher(pass, pass).matches(file1) must be(true)

        EasyMock.verify(file1)
    }

}
