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
package org.devzendo.archivect.gui

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.notNullValue
import java.util.{ List, ArrayList, Arrays }
import scala.collection.mutable.ListBuffer
import org.scalatest.junit.{ AssertionsForJUnit, MustMatchersForJUnit }
import org.junit.Assert._
import org.junit.{ Test, Before, BeforeClass, Ignore, Rule }
import org.junit.rules.TemporaryFolder

import org.apache.log4j.BasicConfigurator
import java.io.{ File, IOException }

import org.devzendo.archivect.prefs.{ ArchivectPrefs, DefaultArchivectPrefs }
import org.devzendo.commoncode.logging.LoggingUnittestHelper

/**
 * Tests the persistence of geometry storage via the adapter to ArchivectPrefs.
 * 
 * @author matt
 *
 */
object TestArchivectWindowGeometryStorePersistenceS {
    val WINDOW_GEOMETRY = "20,20,700,500"
    val FOO = "foo"

    @BeforeClass
    def setupLogging() {
        LoggingUnittestHelper.setupLogging()
    }
}

import TestArchivectWindowGeometryStorePersistenceS._

class TestArchivectWindowGeometryStorePersistenceS extends TemporaryDirUnittest with AssertionsForJUnit with MustMatchersForJUnit {
    var persistence: ArchivectWindowGeometryStorePersistence = null

    /**
     * Create a temporary file to hold prefs data, that's deleted after
     * the JVM exits.
     * 
     * @throws IOException on failure
     */
    @Before
    @throws(classOf[IOException])
    def setUp() {
        val mTempFile: File = tempDir.newFile("archivect-unit-test.prefs").getAbsoluteFile()
        val prefs: ArchivectPrefs = new DefaultArchivectPrefs(mTempFile.getAbsolutePath())
        persistence = new ArchivectWindowGeometryStorePersistence(prefs)
    }
    
    @Test
    def testGetDefaultGeometry() {
        persistence.getWindowGeometry(FOO) must equal("")
    }
    
    @Test
    def testGetStoredGeometry() {
        persistence.setWindowGeometry(FOO, WINDOW_GEOMETRY)
        persistence.getWindowGeometry(FOO) must equal (WINDOW_GEOMETRY)
    }
}