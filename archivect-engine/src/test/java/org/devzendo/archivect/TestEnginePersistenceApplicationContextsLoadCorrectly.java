/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.devzendo.archivect;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.devzendo.archivect.destinations.DefaultDestinations;
import org.devzendo.archivect.prefs.PrefsLocationFactory;
import org.devzendo.commonapp.prefs.DefaultPrefsLocation;
import org.devzendo.commonapp.spring.springloader.SpringLoader;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Can the Archivect Persistence Application Contexts be loaded successfully?
 * 
 * @author matt
 * 
 */
public final class TestEnginePersistenceApplicationContextsLoadCorrectly {
    private SpringLoader springLoader;

    @BeforeClass
    public static void setupLogging() {
        LoggingUnittestHelper.setupLogging();
    }

    @Rule
    public final TemporaryFolder mTempDir = new TemporaryFolder();

    private File mArchivectSubDir;

    /**
     * Create a temporary file to hold persistent data, that's deleted after the
     * JVM exits.
     * 
     * @throws IOException
     *         on failure
     */
    @Before
    public void setUp() throws IOException {
        final List<String> applicationContexts = new ArrayList<String>();
        applicationContexts.addAll(Arrays
                .asList(
                    "org/devzendo/archivect/ArchivectEngineTestPrefsLocation.xml",
                    "org/devzendo/archivect/ArchivectEnginePersistence.xml"));
        springLoader = new ArchivectSpringLoaderInitialiser(applicationContexts)
                .getSpringLoader();
        mTempDir.create();
        mArchivectSubDir = new File(mTempDir.getRoot(), ".archivect");
        mArchivectSubDir.mkdir();
        final PrefsLocationFactory locationFactory = springLoader.getBean("&prefsLocation", PrefsLocationFactory.class);
        locationFactory.setDirectory(mTempDir.getRoot(), ".archivect", "archivect-unit-test-persistence.xml");
    }

    @After
    public void tearDown() throws InterruptedException {
        mTempDir.delete();
    }

    @Test
    public void prefsLocationOk() {
        assertThat(springLoader.getBean("prefsLocation", DefaultPrefsLocation.class),
            notNullValue());
    }

    @Test
    public void destinationsLoadsAndCreatesEmptyFileOk() {
        final File destinationsFile = new File(mArchivectSubDir, "destinations.xml");
        assertThat(destinationsFile.exists(), is(false));
        final DefaultDestinations destinations = springLoader.getBean("destinations", DefaultDestinations.class);
        assertThat(destinations, notNullValue());
        assertThat(destinationsFile.exists(), is(true));
    }
}
