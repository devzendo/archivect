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
import org.devzendo.archivect.filesystemaccess.FileSystemAccessFactory
import org.devzendo.archivect.finder.Finder
import org.devzendo.archivect.model2finder.FinderInitialiser
import org.devzendo.archivect.rule.RuleCompiler
import org.devzendo.commonapp.prefs.{ DefaultPrefsLocation, PrefsFactory, PrefsInstantiator }

import org.devzendo.commonapp.prefs.LoggingPrefsStartupHelper
import org.devzendo.commonapp.spring.springloader.SpringLoader

object TestEngineApplicationContextsLoadCorrectly {
    val springLoader: SpringLoader = setup()
    
    def setup(): SpringLoader = {
        BasicConfigurator.configure()
        
        val applicationContexts: List[String] = new ArrayList[String]()
        ArchivectEngineApplicationContexts.getApplicationContexts().foreach(c => applicationContexts.add(c))
        new ArchivectSpringLoaderInitialiser(applicationContexts).getSpringLoader
    }
}
import TestEngineApplicationContextsLoadCorrectly._

class TestEngineApplicationContextsLoadCorrectly extends AssertionsForJUnit with MustMatchersForJUnit {
    
    @Test
    def applicationContextsLoadCorrectly() {
        assertThat(springLoader, notNullValue());
        // and no exceptions thrown.
    }
    
    @Test
    def prefsLocationOk() {
        assertThat(springLoader.getBean("prefsLocation", classOf[DefaultPrefsLocation]), notNullValue())
    }
    
    @Test
    def prefsFactoryOk() {
        assertThat(springLoader.getBean("&prefs", classOf[PrefsFactory]), notNullValue())
    }

    @Test
    def prefsInstantiatorOk() {
        assertThat(springLoader.getBean("prefsInstantiator", classOf[PrefsInstantiator]), notNullValue())
    }

    @Test
    def ruleCompilerOk() {
        assertThat(springLoader.getBean("ruleCompiler", classOf[RuleCompiler]), notNullValue())
    }

    @Test
    def fileSystemAccessFactoryOk() {
        assertThat(springLoader.getBean("&fileSystemAccess", classOf[FileSystemAccessFactory]), notNullValue())
    }

    @Test
    def finderOk() {
        assertThat(springLoader.getBean("finder", classOf[Finder]), notNullValue())
    }

    @Test
    def finderInitialiserOk() {
        assertThat(springLoader.getBean("finderInitialiser", classOf[FinderInitialiser]), notNullValue())
    }
}