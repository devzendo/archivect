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

import javax.swing.JFrame
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

import org.apache.log4j.Logger

import org.devzendo.commonapp.gui.WindowGeometryStore

object MainFrameCloseActionListener {
    private val LOGGER = Logger.getLogger(classOf[MainFrameCloseActionListener])
}

class MainFrameCloseActionListener(val windowGeometryStore: WindowGeometryStore, val mainFrame: JFrame) extends ActionListener {

  def actionPerformed(e: ActionEvent): Unit = { 
      MainFrameCloseActionListener.LOGGER.debug("Saving main frame geometry")
      windowGeometryStore.saveGeometry(mainFrame)
      MainFrameCloseActionListener.LOGGER.debug("Disposing main frame")
      mainFrame.dispose()
  }
}