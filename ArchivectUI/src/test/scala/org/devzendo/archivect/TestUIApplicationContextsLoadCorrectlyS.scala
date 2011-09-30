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
 
package org.devzendo.archivect

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.notNullValue
import java.util.{ List, ArrayList, Arrays }
import scala.collection.mutable.ListBuffer
import org.scalatest.junit.{ AssertionsForJUnit, MustMatchersForJUnit }
import org.junit.Assert._
import org.junit.{ Test, Before, BeforeClass, Ignore }
import org.apache.log4j.BasicConfigurator
import org.devzendo.archivect.finder.Finder
import org.devzendo.archivect.model2finder.FinderInitialiser
import org.devzendo.archivect.rule.RuleCompiler
import org.devzendo.commonapp.prefs.{ DefaultPrefsLocation, PrefsFactory, PrefsInstantiator }

import org.devzendo.archivect.gui.{ ArchivectMainFrameFactory, ArchivectMainPanel, LifecycleStartupAWTEventListener, MainFrameCloseActionListener }
import org.devzendo.archivect.gui.menu.{ FileMenu, Menu }
import org.devzendo.archivect.gui.recent.RecentJobsList
import org.devzendo.archivect.gui.startup.StartupWizard
import org.devzendo.archivect.lifecycle.{ StartupWizardStartingLifecycle, WizardInitialisingLifecycle }
import org.devzendo.commonapp.gui.menu.MenuWiring
import org.devzendo.commonapp.lifecycle.LifecycleManager
import org.devzendo.commonapp.prefs.GuiPrefsStartupHelper
import org.devzendo.commonapp.spring.springloader.SpringLoader

import org.devzendo.commonapp.prefs.LoggingPrefsStartupHelper
import org.devzendo.commonapp.spring.springloader.SpringLoader

object TestUIApplicationContextsLoadCorrectlyS {
    val springLoader: SpringLoader = setup()
    
    def setup(): SpringLoader = {
        BasicConfigurator.configure()
        
        val applicationContexts: List[String] = new ArrayList[String]()
        ArchivectEngineApplicationContexts.getApplicationContexts().foreach(c => applicationContexts.add(c))
        ArchivectUIApplicationContexts.getApplicationContexts().foreach(c => applicationContexts.add(c))
        new ArchivectSpringLoaderInitialiser(applicationContexts).getSpringLoader()
    }
}
import TestUIApplicationContextsLoadCorrectlyS._

class TestUIApplicationContextsLoadCorrectlyS extends AssertionsForJUnit with MustMatchersForJUnit {
    
    @Test
    def applicationContextsLoadCorrectly() {
        assertThat(springLoader, notNullValue());
        // and no exceptions thrown.
    }
    
    @Test
    def archivectMainFrameFactoryOk() {
        assertThat(springLoader.getBean("archivectMainFrameFactory", classOf[ArchivectMainFrameFactory]), notNullValue())
    }

    @Test
    def guiPrefsStartupHelperOk() {
        assertThat(springLoader.getBean("guiPrefsStartupHelper", classOf[GuiPrefsStartupHelper]), notNullValue())
    }

    @Test
    def menuOk() {
        assertThat(springLoader.getBean("menu", classOf[Menu]), notNullValue())
    }

    @Test
    def fileMenuOk() {
        assertThat(springLoader.getBean("fileMenu", classOf[FileMenu]), notNullValue())
    }

    @Test
    def menuWiringOk() {
        assertThat(springLoader.getBean("menuWiring", classOf[MenuWiring]), notNullValue())
    }

    @Test
    def recentJobsListOk() {
        assertThat(springLoader.getBean("recentJobsList", classOf[RecentJobsList]), notNullValue())
    }

    @Test
    def mainFrameCloseActionListenerOk() {
        assertThat(springLoader.getBean("mainFrameCloseActionListener", classOf[MainFrameCloseActionListener]), notNullValue())
    }
    
    @Test
    def lifecycleStartupAWTEventListenerOk() {
        assertThat(springLoader.getBean("lifecycleStartupAWTEventListener", classOf[LifecycleStartupAWTEventListener]), notNullValue())
    }
    
    @Test
    def lifecycleManagerOk() {
        assertThat(springLoader.getBean("lifecycleManager", classOf[LifecycleManager]), notNullValue())
    }

    @Test
    def mainPanelOk() {
        assertThat(springLoader.getBean("mainPanel", classOf[ArchivectMainPanel]), notNullValue())
    }

    @Test
    def startupWizardOk() {
        assertThat(springLoader.getBean("startupWizard", classOf[StartupWizard]), notNullValue())
    }

    @Test
    def wizardInitialisingLifecycleOk() {
        assertThat(springLoader.getBean("wizardInitialisingLifecycle", classOf[WizardInitialisingLifecycle]), notNullValue())
    }

    @Test
    def startupWizardStartingLifecycleOk() {
        assertThat(springLoader.getBean("startupWizardStartingLifecycle", classOf[StartupWizardStartingLifecycle]), notNullValue())
    }
}