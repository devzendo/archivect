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
import org.devzendo.archivect.gui.ArchivectMainFrameFactory;
import org.devzendo.archivect.gui.ArchivectMainPanel;
import org.devzendo.archivect.gui.LifecycleStartupAWTEventListener;
import org.devzendo.archivect.gui.MainFrameCloseActionListener;
import org.devzendo.archivect.gui.menu.FileMenu;
import org.devzendo.archivect.gui.menu.Menu;
import org.devzendo.archivect.gui.recent.RecentJobsList;
import org.devzendo.archivect.gui.startup.StartupWizard;
import org.devzendo.archivect.lifecycle.StartupWizardStartingLifecycle;
import org.devzendo.archivect.lifecycle.WizardInitialisingLifecycle;
import org.devzendo.commonapp.gui.menu.MenuWiring;
import org.devzendo.commonapp.lifecycle.LifecycleManager;
import org.devzendo.commonapp.prefs.GuiPrefsStartupHelper;
import org.devzendo.commonapp.spring.springloader.SpringLoader;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author matt
 *
 */
public final class TestUIApplicationContextsLoadCorrectly {
    private static SpringLoader springLoader;

    @BeforeClass
    public static void setup() {
        BasicConfigurator.configure();
        
        final List<String> applicationContexts = new ArrayList<String>();
        applicationContexts.addAll(Arrays.asList(ArchivectEngineApplicationContexts.getApplicationContexts()));
        applicationContexts.addAll(Arrays.asList(ArchivectUIApplicationContextsS.getApplicationContexts()));
        springLoader = new ArchivectSpringLoaderInitialiser(applicationContexts).getSpringLoader();
    }

    @Test
    public void applicationContextsLoadCorrectly() {
        assertThat(springLoader, notNullValue());
        // and no exceptions thrown.
    }
    
    @Test
    public void archivectMainFrameFactoryOk() {
        assertThat(springLoader.getBean("archivectMainFrameFactory", ArchivectMainFrameFactory.class), notNullValue());
    }

    @Test
    public void guiPrefsStartupHelperOk() {
        assertThat(springLoader.getBean("guiPrefsStartupHelper", GuiPrefsStartupHelper.class), notNullValue());
    }

    @Test
    public void menuOk() {
        assertThat(springLoader.getBean("menu", Menu.class), notNullValue());
    }

    @Test
    public void fileMenuOk() {
        assertThat(springLoader.getBean("fileMenu", FileMenu.class), notNullValue());
    }

    @Test
    public void menuWiringOk() {
        assertThat(springLoader.getBean("menuWiring", MenuWiring.class), notNullValue());
    }

    @Test
    public void recentJobsListOk() {
        assertThat(springLoader.getBean("recentJobsList", RecentJobsList.class), notNullValue());
    }

    @Test
    public void mainFrameCloseActionListenerOk() {
        assertThat(springLoader.getBean("mainFrameCloseActionListener", MainFrameCloseActionListener.class), notNullValue());
    }
    
    @Test
    public void lifecycleStartupAWTEventListenerOk() {
        assertThat(springLoader.getBean("lifecycleStartupAWTEventListener", LifecycleStartupAWTEventListener.class), notNullValue());
    }
    
    @Test
    public void lifecycleManagerOk() {
        assertThat(springLoader.getBean("lifecycleManager", LifecycleManager.class), notNullValue());
    }

    @Test
    public void mainPanelOk() {
        assertThat(springLoader.getBean("mainPanel", ArchivectMainPanel.class), notNullValue());
    }

    @Test
    public void startupWizardOk() {
        assertThat(springLoader.getBean("startupWizard", StartupWizard.class), notNullValue());
    }

    @Test
    public void wizardInitialisingLifecycleOk() {
        assertThat(springLoader.getBean("wizardInitialisingLifecycle", WizardInitialisingLifecycle.class), notNullValue());
    }

    @Test
    public void startupWizardStartingLifecycleOk() {
        assertThat(springLoader.getBean("startupWizardStartingLifecycle", StartupWizardStartingLifecycle.class), notNullValue());
    }
}
