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

import java.io.IOException;

import org.devzendo.commonapp.prefs.Prefs;
import org.devzendo.commonapp.prefs.PrefsLocation;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.integration.junit4.JMock;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;


/**
 * Tests the instantiation of prefs.
 * 
 * @author matt
 *
 */
@RunWith(JMock.class)
public final class TestPrefsInstantiator {
    private static final String PREFS_FILE = "archivect.prefs";
    private static final String PREFS_DIRECTORY = ".archivect";

    /**
     * 
     */
    @BeforeClass
    public static void setupLogging() {
        LoggingUnittestHelper.setupLogging();
    }

    @Rule
    private final TemporaryFolder tempDir = new TemporaryFolder();

    /**
     * @throws IOException never
     */
    @Before
    public void setUp() throws IOException {
        tempDir.create();
    }
    /**
     * 
     */
    @After
    public void tearDown() {
        tempDir.delete();
    }
    
    /**
     * @throws IOException never
     * 
     */
    @Test
    public void prefsAreInstantiatedCorrectly() throws IOException {
        final PrefsLocation prefsLocation = new PrefsLocation(PREFS_DIRECTORY, PREFS_FILE, tempDir.getRoot().getAbsolutePath());

        final DefaultPrefsInstantiator prefsInstantiator = new DefaultPrefsInstantiator();
        final Prefs prefs = prefsInstantiator.instantiatePrefs(prefsLocation);
        MatcherAssert.assertThat(prefs, Matchers.notNullValue(Prefs.class));
        // TODO what's the prefs like? How can I test for something here?
    }
}
