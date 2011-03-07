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

import org.apache.log4j.BasicConfigurator;
import org.devzendo.commonapp.prefs.PrefsFactory;
import org.devzendo.commonapp.prefs.PrefsInstantiator;
import org.devzendo.commonapp.prefs.PrefsLocation;
import org.devzendo.commonapp.spring.springloader.SpringLoader;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Can the Archivect Application Contexts be loaded successfully?
 * @author matt
 *
 */
public final class TestEngineApplicationContextsLoadCorrectly {
    private static SpringLoader springLoader;

    @BeforeClass
    public static void setup() {
        BasicConfigurator.configure();
        
        final List<String> applicationContexts = new ArrayList<String>();
        applicationContexts.addAll(Arrays.asList(ArchivectEngineApplicationContexts.getApplicationContexts()));
        springLoader = new ArchivectSpringLoaderInitialiser(applicationContexts).getSpringLoader();
    }
    
    @Test
    public void loadSpringLoaderOK() {
        final List<String> applicationContexts = new ArrayList<String>();
        applicationContexts.addAll(Arrays.asList(ArchivectEngineApplicationContexts.getApplicationContexts()));
        new ArchivectSpringLoaderInitialiser(applicationContexts).getSpringLoader();
        // shouldn't throw!
    }
    
    @Test
    public void prefsLocationOk() {
        assertThat(springLoader.getBean("prefsLocation", PrefsLocation.class), notNullValue());
    }
    
    @Test
    public void prefsFactoryOk() {
        assertThat(springLoader.getBean("&prefs", PrefsFactory.class), notNullValue());
    }

    @Test
    public void prefsInstantiatorOk() {
        assertThat(springLoader.getBean("prefsInstantiator", PrefsInstantiator.class), notNullValue());
    }
}
