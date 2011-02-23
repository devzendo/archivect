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

import java.awt.{Dimension, CardLayout}
import javax.swing.{JPanel, JButton}

import org.devzendo.commonapp.gui.GUIUtils

object ArchivectMainPanel {
    val BlankPanelName = "*blank*panel*"
}
class ArchivectMainPanel {
    private val cardLayout = new CardLayout()
    val panel = new JPanel(cardLayout)
    panel.setPreferredSize(new Dimension(640, 480))
    panel.add(new JPanel(), ArchivectMainPanel.BlankPanelName)
    cardLayout.show(panel, ArchivectMainPanel.BlankPanelName)
    
    def addPanel(panelName: String, newPanel: JPanel) = {
        GUIUtils.runOnEventThread(new Runnable() {
            def run = {
                panel.add(newPanel, panelName)
            }
        })
    }
    def mainPanel = panel
    def switchToPanel(panelName: String) = {
        GUIUtils.runOnEventThread(new Runnable() {
            def run = {
                cardLayout.show(panel, panelName)
            }
        })
    }
}