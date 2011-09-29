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

import java.awt.{ AWTEvent, Toolkit }
import java.io.File
import java.util.{ ArrayList, Arrays, List }

import javax.swing.JFrame

import org.apache.log4j.{ Level, Logger }
import org.devzendo.archivect.gui.{ ArchivectMainFrameFactory, LifecycleStartupAWTEventListener, MainFrameCloseActionListener }
import org.devzendo.archivect.gui.menu.{ ArchivectMenuIdentifiers, Menu }
import org.devzendo.commonapp.gui.{ Beautifier, GUIUtils, ThreadCheckingRepaintManager }
import org.devzendo.commonapp.gui.menu.MenuWiring
import org.devzendo.commonapp.prefs.GuiPrefsStartupHelper
import org.devzendo.commonapp.spring.springloader.SpringLoader
import org.devzendo.commoncode.logging.Logging

/**
 * The Archivect UI.
 * 
 * @author matt
 *
 */
class ArchivectUIMain {
    
}
object ArchivectUIMain {
    private val LOGGER = Logger.getLogger(classOf[ArchivectUIMain])

    /**
     * @param args the command line arguments.
     */
    def main(args: Array[String]) {
        val logging = Logging.getInstance()
        val argList: java.util.ArrayList[String] = new java.util.ArrayList[String]()
        args.foreach(s => argList.add(s))
        val finalArgList = logging.setupLoggingFromArgs(argList)
        logging.setPackageLoggingLevel("org.springframework", Level.WARN)
        LOGGER.debug("Starting Archivect UI")
        
        val javaLibraryPath = System.getProperty("java.library.path")
        LOGGER.debug("java.library.path is '" + javaLibraryPath + "'")
        val quaqua = new File(javaLibraryPath, "libquaqua.jnilib")
        LOGGER.debug("Quaqua JNI library exists there (for Mac OS X)? " + quaqua.exists())

        ThreadCheckingRepaintManager.initialise()

        val applicationContexts = new java.util.ArrayList[String]()
        ArchivectEngineApplicationContexts.getApplicationContexts().foreach(c => applicationContexts.add(c))
        ArchivectUIApplicationContextsS.getApplicationContexts().foreach(c => applicationContexts.add(c))
        val springLoader = new ArchivectSpringLoaderInitialiser(applicationContexts).getSpringLoader()

        val prefsStartupHelper: GuiPrefsStartupHelper = springLoader.getBean("guiPrefsStartupHelper", classOf[GuiPrefsStartupHelper])
        prefsStartupHelper.initialisePrefs()

        LOGGER.debug("Application contexts and prefs initialised")
        // Sun changed their recommendations and now recommends the UI be built
        // on the EDT, so I think flagging creation on non-EDT is OK.
        // "We used to say that you could create the GUI on the main thread as
        // long as you didn't modify components that had already been realized.
        // While this worked for most applications, in certain situations it
        // could cause problems."
        // http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
        // So let's create it on the EDT anyway
        //
        GUIUtils.runOnEventThread(new Runnable() {
            def run = {
                try {
                    Beautifier.makeBeautiful()

                    // Process command line
                    for (i <- 0 until finalArgList.size()) {
                        LOGGER.debug("arg " + i + " = " + finalArgList.get(i) + "'")
                    }

                    val frameFactory = springLoader.getBean(
                        "archivectMainFrameFactory",
                        classOf[ArchivectMainFrameFactory])
                    val mainFrame = frameFactory.createFrame
                    
                    val menu = springLoader.getBean("menu", classOf[Menu])
                    menu.initialise
                    mainFrame.setJMenuBar(menu.getMenuBar)

                    val closeAL = springLoader.getBean(
                            "mainFrameCloseActionListener",
                            classOf[MainFrameCloseActionListener])
                    val menuWiring = springLoader.getBean("menuWiring", classOf[MenuWiring])
                    menuWiring.setActionListener(ArchivectMenuIdentifiers.FILE_EXIT, closeAL)

                    val lifecycleStartup = springLoader.getBean(
                        "lifecycleStartupAWTEventListener", 
                        classOf[LifecycleStartupAWTEventListener])
                    Toolkit.getDefaultToolkit().addAWTEventListener(lifecycleStartup, AWTEvent.WINDOW_EVENT_MASK)
                    
                    mainFrame.setVisible(true)
                } catch {
                    case e: Exception =>
                        LOGGER.fatal(e.getMessage(), e)
                        System.exit(1)
                }
            }
        })
    }
}