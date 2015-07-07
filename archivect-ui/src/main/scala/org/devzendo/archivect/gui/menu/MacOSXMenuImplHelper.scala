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

package org.devzendo.archivect.gui.menu

import com.apple.eawt._

import org.apache.log4j.Logger

import org.devzendo.commonapp.gui.menu._

object MacOSXMenuImplHelper {
    private val LOGGER = Logger.getLogger(classOf[MacOSXMenuImplHelper])
}

class MacOSXMenuImplHelper(val wiring: MenuWiring) extends ApplicationAdapter {
    MacOSXMenuImplHelper.LOGGER.info("Initialising MacOSX-specific menu")
    val macApp = Application.getApplication()
    macApp.addApplicationListener(this)
    
    override def handleAbout(e: ApplicationEvent) = {
        MacOSXMenuImplHelper.LOGGER.info("Handling About menu event")
        wiring.triggerActionListener(ArchivectMenuIdentifiers.HELP_ABOUT)
        e.setHandled(true)
    }
    override def handlePreferences(e: ApplicationEvent) = {
        MacOSXMenuImplHelper.LOGGER.info("Handling Preferences menu event")
        wiring.triggerActionListener(ArchivectMenuIdentifiers.TOOLS_OPTIONS)
        e.setHandled(true)
    }
    override def handleQuit(e: ApplicationEvent) = {
        MacOSXMenuImplHelper.LOGGER.info("Handling Quit menu event")
        wiring.triggerActionListener(ArchivectMenuIdentifiers.FILE_EXIT)
        e.setHandled(true)
    }
}