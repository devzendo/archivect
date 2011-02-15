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
 
package org.devzendo.archivect.gui

import java.awt.AWTEvent
import java.awt.event.{AWTEventListener, WindowEvent}
import javax.swing.JFrame

import org.apache.log4j.Logger

import org.devzendo.commonapp.gui.CursorManager
import org.devzendo.commonapp.gui.SwingWorker
import org.devzendo.commonapp.lifecycle.LifecycleManager

object LifecycleStartupAWTEventListener {
    private val LOGGER = Logger.getLogger(classOf[LifecycleStartupAWTEventListener])
}

/**
 * The LifecycleStartupAWTEventListener is attached as a listener
 * to the main JFrame, and listens for it becoming visible. At
 * this point, it triggers the Lifecycle startup on a separate
 * thread, surrounding this with an hourglass cursor.
 * 
 * @author matt
 *
 */
class LifecycleStartupAWTEventListener(
    val mainFrame: JFrame,
    val cursorManager: CursorManager,
    val lifecycleManager : LifecycleManager) extends AWTEventListener {
    
        
    /**
     * {@inheritDoc}
     */
    def eventDispatched(event: AWTEvent) = {
        LifecycleStartupAWTEventListener.LOGGER.debug("Event received")
        if (event.getID() == WindowEvent.WINDOW_OPENED && event.getSource().equals(mainFrame)) {
            startLifecycle()
        }
    }
    
    private def startLifecycle() = {
        cursorManager.hourglass(this.getClass.getSimpleName)
        val worker = new SwingWorker() {
            override def construct(): Object = {
                Thread.currentThread().setName("Lifecycle Startup")
                LifecycleStartupAWTEventListener.LOGGER.info("Lifecycle manager startup...")
                lifecycleManager.startup()
                LifecycleStartupAWTEventListener.LOGGER.info("...end of lifecycle manager startup")
                return null
            }
            
            override def finished = {
                cursorManager.normal(this.getClass.getSimpleName)
            }
        }
        worker.start()
    }
}