/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
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

package org.devzendo.archivect.prefs;

import java.io.File;
import java.io.IOException;

import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;



/**
 * Tests user preference storage API
 * 
 * @author matt
 *
 */
public final class TestPrefs {
    private static final String WINDOW_GEOMETRY = "20,20,700,500";
    private static final String FOO = "foo";
    private ArchivectPrefs prefs;

    /**
     * 
     */
    @BeforeClass
    public static void setupLogging() {
        LoggingUnittestHelper.setupLogging();
    }
    
    /**
     * 
     */
    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    /**
     * Create a temporary file to hold prefs data, that's deleted after
     * the JVM exits.
     * 
     * @throws IOException on failure
     */
    @Before
    public void setUp() throws IOException {
        tempDir.create();
        System.out.println("the temp folder is " + tempDir.getRoot().getAbsolutePath());

        final File mTempFile = tempDir.newFile("archivect-unit-test.prefs").getAbsoluteFile();
        prefs = new DefaultArchivectPrefs(mTempFile.getAbsolutePath());
    }
    
    /**
     * 
     */
    @Test
    public void testGetDefaultGeometry() {
        Assert.assertEquals("", prefs.getWindowGeometry(FOO));
    }
    
    /**
     * 
     */
    @Test
    public void testGetStoredGeometry() {
        prefs.setWindowGeometry(FOO, WINDOW_GEOMETRY);
        Assert.assertEquals(WINDOW_GEOMETRY, prefs.getWindowGeometry(FOO));
    }
}
