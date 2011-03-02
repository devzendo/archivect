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
package org.devzendo.archivect.destinations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests that destinations can be persisted.
 * 
 * @author matt
 *
 */
public final class TestDestinations {
    private static final Logger LOGGER = Logger
            .getLogger(TestDestinations.class);
    private Destinations mDestinations;
    private File mTempFile;
    
    @BeforeClass
    public static void setupLogging() {
        LoggingUnittestHelper.setupLogging();
    }
    
    @Rule
    public final TemporaryFolder mTempDir = new TemporaryFolder();

    /**
     * Create a temporary file to hold prefs data, that's deleted after
     * the JVM exits.
     * 
     * @throws IOException on failure
     */
    @Before
    public void setUp() throws IOException {
        mTempDir.create();
        LOGGER.debug("the temp folder is " + mTempDir.getRoot().getAbsolutePath());

        mTempFile = new File(mTempDir.getRoot(), "archivect-unit-test-destinations.xml");
        assertThat(mTempFile.exists(), is(false));
        mDestinations = new DefaultDestinations(mTempFile.getAbsolutePath());
    }
    
    @After
    public void tearDown() throws InterruptedException {
        mTempDir.delete();
    }

    @Test
    public void existenceAndSizeCorrectAfterInstantiation() {
        assertThat(mTempFile.exists(), is(true));
        assertThat(mDestinations.size(), is(0));
    }
}
