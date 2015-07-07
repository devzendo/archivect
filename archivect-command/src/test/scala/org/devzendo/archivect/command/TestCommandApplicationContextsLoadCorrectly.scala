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
 
package org.devzendo.archivect.command

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.notNullValue
import java.util.{ List, ArrayList, Arrays }
import scala.collection.mutable.ListBuffer
import org.scalatest.junit.{ AssertionsForJUnit, MustMatchersForJUnit }
import org.junit.Assert._
import org.junit.{ Test, Before, BeforeClass, Ignore }
import org.apache.log4j.BasicConfigurator
import org.devzendo.archivect.ArchivectCommandApplicationContexts
import org.devzendo.archivect.{ ArchivectEngineApplicationContexts, ArchivectSpringLoaderInitialiser }
import org.devzendo.commonapp.prefs.LoggingPrefsStartupHelper
import org.devzendo.commonapp.spring.springloader.SpringLoader

object TestCommandApplicationContextsLoadCorrectly {
    val springLoader: SpringLoader = setup()
    
    def setup(): SpringLoader = {
        BasicConfigurator.configure()
        
        val applicationContexts: List[String] = new ArrayList[String]()
        ArchivectEngineApplicationContexts.getApplicationContexts().foreach(c => applicationContexts.add(c))
        ArchivectCommandApplicationContexts.getApplicationContexts().foreach(c => applicationContexts.add(c))
        new ArchivectSpringLoaderInitialiser(applicationContexts).getSpringLoader()
    }
}
import TestCommandApplicationContextsLoadCorrectly._

class TestCommandApplicationContextsLoadCorrectly extends AssertionsForJUnit with MustMatchersForJUnit {
    
    @Test
    def applicationContextsLoadCorrectly() {
        assertThat(springLoader, notNullValue());
        // and no exceptions thrown.
    }
    
    @Test
    def loggingPrefsStartupHelperOk() {
        assertThat(springLoader.getBean("loggingPrefsStartupHelper", classOf[LoggingPrefsStartupHelper]), notNullValue())
    }
    
    @Test
    def commandLineParserOk() {
        assertThat(springLoader.getBean("commandLineParser", classOf[CommandLineParser]), notNullValue())
    }
}