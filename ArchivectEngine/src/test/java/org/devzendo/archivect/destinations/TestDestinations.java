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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.devzendo.commoncode.patterns.observer.Observer;
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
        assertThat(mTempDir.getRoot().exists(), is(true));
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
    
    @Test
    public void aLocalDestinationCanBeAddedAndIsPersisted() {
        mDestinations.addDestination(localDestination());
        
        assertThat(mDestinations.size(), is(1));
        final LocalDestination destination = (LocalDestination) mDestinations.getDestination(0);
        assertThat(destination.name(), equalTo("localtmp"));
        assertThat(destination.localPath(), equalTo("/tmp/foo"));
        
        final Destinations newDestinations = new DefaultDestinations(mTempFile.getAbsolutePath());
        assertThat(newDestinations.size(), is(1));
        final LocalDestination reloadedDestination = (LocalDestination) newDestinations.getDestination(0);
        assertThat(reloadedDestination.name(), equalTo("localtmp"));
        assertThat(reloadedDestination.localPath(), equalTo("/tmp/foo"));
    }

    @Test
    public void aSmbDestinationCanBeAddedAndIsPersisted() {
        mDestinations.addDestination(smbDestination());
        
        assertThat(mDestinations.size(), is(1));
        final SmbDestination destination = (SmbDestination) mDestinations.getDestination(0);
        assertThat(destination.name(), equalTo("smbtmp"));
        assertThat(destination.server(), equalTo("server"));
        assertThat(destination.share(), equalTo("public"));
        assertThat(destination.userName(), equalTo("username"));
        assertThat(destination.password(), equalTo("password"));
        assertThat(destination.localPath(), equalTo("/tmp/foo"));
        
        final Destinations newDestinations = new DefaultDestinations(mTempFile.getAbsolutePath());
        assertThat(newDestinations.size(), is(1));
        final SmbDestination reloadedDestination = (SmbDestination) newDestinations.getDestination(0);
        assertThat(reloadedDestination.name(), equalTo("smbtmp"));
        assertThat(reloadedDestination.server(), equalTo("server"));
        assertThat(reloadedDestination.share(), equalTo("public"));
        assertThat(reloadedDestination.userName(), equalTo("username"));
        assertThat(reloadedDestination.password(), equalTo("password"));
        assertThat(reloadedDestination.localPath(), equalTo("/tmp/foo"));
    }

    @Test
    public void summariesCanBeObtained() {
        mDestinations.addDestination(localDestination());
        mDestinations.addDestination(smbDestination());
        
        final java.util.List<DestinationSummary> summaries = mDestinations.summariesAsList();
        assertThat(summaries.size(), equalTo(2));
        final DestinationSummary ds0 = summaries.get(0);
        assertThat(ds0.name(), equalTo("localtmp"));
        assertThat(ds0.destinationType(), equalTo("local"));
        final DestinationSummary ds1 = summaries.get(1);
        assertThat(ds1.name(), equalTo("smbtmp"));
        assertThat(ds1.destinationType(), equalTo("smb"));
    }
    
    @Test
    public void destinationCanBeRemoved() {
        final LocalDestination localDestination = localDestination();
        mDestinations.addDestination(localDestination);
        assertThat(mDestinations.size(), is(1));
        
        mDestinations.removeDestination(localDestination);
        
        assertThat(mDestinations.size(), is(0));
    }
    
    @Test
    public void addDestinationAndListenerIsNotified() {
        final boolean[] fired = {false};
        mDestinations.addDestinationListener(new Observer<DestinationEvent>() {
            public void eventOccurred(final DestinationEvent observableEvent) {
                if (observableEvent instanceof DestinationAddedEvent) {
                    fired[0] = true;
                }
            }
        });
        assertThat(fired[0], equalTo(false));
        mDestinations.addDestination(localDestination());
        assertThat(fired[0], equalTo(true));
    }

    @Test
    public void removeDestinationAndListenerIsNotified() {
        final boolean[] fired = {false};
        final LocalDestination localDestination = localDestination();
        mDestinations.addDestination(localDestination);
        mDestinations.addDestinationListener(new Observer<DestinationEvent>() {
            public void eventOccurred(final DestinationEvent observableEvent) {
                if (observableEvent instanceof DestinationRemovedEvent) {
                    fired[0] = true;
                }
            }
        });
        assertThat(fired[0], equalTo(false));
        mDestinations.removeDestination(localDestination);
        assertThat(fired[0], equalTo(true));
    }
    
    @Test
    public void duplicateNameIsDetected() {
        final LocalDestination localDestination = localDestination();
        assertThat(mDestinations.destinationNameExists("localtmp"), equalTo(false));
        mDestinations.addDestination(localDestination);
        assertThat(mDestinations.destinationNameExists("localtmp"), equalTo(true));
    }

    private LocalDestination localDestination() {
        return new LocalDestination("localtmp", "/tmp/foo");
    }
    
    private SmbDestination smbDestination() {
        return new SmbDestination("smbtmp", "server", "public", "username", "password", "/tmp/foo");
    }
}
