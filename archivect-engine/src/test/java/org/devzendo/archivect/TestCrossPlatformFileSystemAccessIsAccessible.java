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
package org.devzendo.archivect;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.devzendo.commoncode.logging.CapturingAppender;
import org.devzendo.xpfsa.DefaultFileSystemAccess;
import org.devzendo.xpfsa.FileSystemAccessException;
import org.devzendo.xpfsa.SpaceInvader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Basic test to ensure we have XPFSA.
 * 
 * @author matt
 *
 */
public final class TestCrossPlatformFileSystemAccessIsAccessible {
    private CapturingAppender mCapturingAppender;

    @Before
    public void setupLogging() {
        BasicConfigurator.resetConfiguration();
        mCapturingAppender = new CapturingAppender();
        BasicConfigurator.configure(mCapturingAppender);
        Assert.assertEquals(0, mCapturingAppender.getEvents().size());
    }
    
    @Test
    public void testNativeCall() throws FileSystemAccessException {
        final DefaultFileSystemAccess fsa = new DefaultFileSystemAccess();
        SpaceInvader.logMessage("Hello logger", fsa);
        
        Assert.assertEquals(1, mCapturingAppender.getEvents().size());
        final LoggingEvent loggingEvent = mCapturingAppender.getEvents().get(0);
        Assert.assertEquals(Level.DEBUG, loggingEvent.getLevel());
        Assert.assertEquals("Hello logger", loggingEvent.getMessage().toString());
    }
}
