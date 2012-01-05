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

import java.util.{ ArrayList, Arrays, List }

import org.apache.log4j.{ Level, Logger }
import org.devzendo.archivect.command.{ CommandLineParser, CommandLineException }
import org.devzendo.archivect.finder.Finder
import org.devzendo.archivect.model.CommandModel
import org.devzendo.archivect.model2finder.FinderInitialiser
import org.devzendo.archivect.rule.RuleCompiler
import org.devzendo.commonapp.prefs.LoggingPrefsStartupHelper
import org.devzendo.commonapp.spring.springloader.SpringLoader
import org.devzendo.commoncode.logging.Logging
import org.devzendo.xpfsa.{ DefaultFileSystemAccess, FileSystemAccess, FileSystemAccessException }

import scala.collection.JavaConversions

/**
 * The Archivect main command line program.
 * 
 * @author matt
 *
 */
class ArchivectMain {
    
}
object ArchivectMain {
    private val LOGGER = Logger.getLogger(classOf[ArchivectMain])

    /**
     * @param args the command line arguments.
     */
    def main(args: Array[String]) {
        val logging = Logging.getInstance()
        val argList: java.util.ArrayList[String] = new java.util.ArrayList[String]()
        args.foreach(s => argList.add(s))
        val finalArgList = logging.setupLoggingFromArgs(argList)
        logging.setPackageLoggingLevel("org.springframework", Level.WARN)
        LOGGER.debug("Starting Archivect command line tool")
        
        val applicationContexts = new java.util.ArrayList[String]()
        ArchivectEngineApplicationContexts.getApplicationContexts().foreach(c => applicationContexts.add(c))
        ArchivectCommandApplicationContexts.getApplicationContexts().foreach(c => applicationContexts.add(c))
        val springLoader = new ArchivectSpringLoaderInitialiser(applicationContexts).getSpringLoader()

        val prefsStartupHelper: LoggingPrefsStartupHelper = springLoader.getBean("loggingPrefsStartupHelper", classOf[LoggingPrefsStartupHelper])
        prefsStartupHelper.initialisePrefs()
        
        LOGGER.debug("Application contexts and prefs initialised");

        val commandLineParser: CommandLineParser = springLoader.getBean("commandLineParser", classOf[CommandLineParser])
        val ruleCompiler: RuleCompiler = springLoader.getBean("ruleCompiler", classOf[RuleCompiler])
        try {
            val operation: CommandModel = commandLineParser.parse(finalArgList)
            val finder: Finder = springLoader.getBean("finder", classOf[Finder])
            val finderInitialiser: FinderInitialiser = springLoader.getBean("finderInitialiser", classOf[FinderInitialiser])
            finderInitialiser.populateFromModel(operation)
            
            val fileSystemAccess: DefaultFileSystemAccess = new DefaultFileSystemAccess()
        } catch {
            case e: CommandLineException =>
                LOGGER.error(e.getMessage())
            case fe: FileSystemAccessException =>
                LOGGER.fatal("Could not load CrossPlatformFileSystemAccess library: " + fe.getMessage(), fe)
        }
    }
}