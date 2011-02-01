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

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants

import org.apache.log4j.Logger
import org.devzendo.commonapp.gui.menu.MenuWiring
import org.devzendo.commonapp.gui.WindowGeometryStore
import org.devzendo.commoncode.resource.ResourceLoader

object ArchivectMainFrame {
    private val LOGGER = Logger.getLogger(classOf[ArchivectMainFrame])
    private val MAIN_FRAME_NAME = "main"
}

/**
 * The Archivect UI Main Frame, saves its geometry, and handles closing.
 * Construct the main application frame, given the geometry store in which
 * its initial size and location will be loaded, and persisted on close.
 * 
 * @author matt
 * @param windowGeometryStore the geometry store
 */
class ArchivectMainFrame(val windowGeometryStore: WindowGeometryStore, val menuWiring: MenuWiring) extends JFrame {
    setIconImage(ResourceLoader.createResourceImageIcon("org/devzendo/archivect/icons/application16x16.gif").getImage())
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)

    setName(ArchivectMainFrame.MAIN_FRAME_NAME)
    setLayout(new BorderLayout())

    // TODO inject me....
    val mainPanel = new JPanel() // TODO to be replaced by the main app panel
    mainPanel.setPreferredSize(new Dimension(640, 480))
    add(mainPanel, BorderLayout.CENTER)
        
    loadInitialGeometry
    setupGeometrySaveOnMoveOnClose

    def loadInitialGeometry = {
        if (!windowGeometryStore.hasStoredGeometry(this)) {
            pack
        }
        windowGeometryStore.loadGeometry(this)
    }
    
    def setupGeometrySaveOnMoveOnClose = {
        addWindowListener(new WindowAdapter() {
            override def windowClosing(e: WindowEvent) {
                ArchivectMainFrame.LOGGER.debug("Detected window closing; triggering action listener for FileExit")
                menuWiring.triggerActionListener(ArchivectMenuIdentifiers.FILE_EXIT)
            }
            override def windowClosed(e: WindowEvent ) {
                ArchivectMainFrame.LOGGER.debug("Detected window closed")
        }});
    }
}