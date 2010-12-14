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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.devzendo.archivect.gui.ArchivectMainFrameFactory;
import org.devzendo.commonapp.spring.springloader.SpringLoader;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author matt
 *
 */
public final class TestApplicationContextsLoadCorrectly {
    
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
    @Test
    public void applicationContextsLoadCorrectly() {
        assertThat(springLoader(), notNullValue());
        // and no exceptions thrown.
    }
    
    /**
     * 
     */
    @Test
    public void archivectMainFrameFactoryOk() {
        assertThat(springLoader().getBean("archivectMainFrameFactory", ArchivectMainFrameFactory.class), notNullValue());
    }

    private SpringLoader springLoader() {
        final List<String> applicationContexts = new ArrayList<String>();
        applicationContexts.addAll(Arrays.asList(ArchivectEngineApplicationContexts.getApplicationContexts()));
        applicationContexts.addAll(Arrays.asList(ArchivectUIApplicationContexts.getApplicationContexts()));
        final SpringLoader springLoader = new ArchivectSpringLoaderInitialiser(applicationContexts).getSpringLoader();
        return springLoader;
    }
}
