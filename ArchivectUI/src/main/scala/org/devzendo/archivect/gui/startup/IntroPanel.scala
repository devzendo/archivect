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
 
package org.devzendo.archivect.gui.startup

import java.awt.BorderLayout
import javax.swing.{JPanel, JTextPane, SwingUtilities}

import org.devzendo.commonapp.gui.GUIUtils
import org.netbeans.spi.wizard.{WizardController, WizardPage}

object IntroPanel {
    def getDescription = {
        "Welcome to Archivect" 
    }
}
class IntroPanel extends WizardPage {
    assert(SwingUtilities.isEventDispatchThread())
    
    setLayout(new BorderLayout())
    val textPane = new JTextPane()
    textPane.setBackground(getBackground())
    textPane.setContentType("text/html")

    textPane.setText(getText())
    add(textPane, BorderLayout.WEST)
    
    setProblem(null)
    setForwardNavigationMode(WizardController.MODE_CAN_CONTINUE)
    
    def getText() = {
        "<font face='Helvetica, Arial' size='-1'>" +
        "Welcome to Archivect, a personal archive/backup application!<p><br>" + 
        "Before you can backup or restore your data, you must set<br>" +
        "up one or more <em>destinations</em>. These are where your data is<br>" +
        "backed up or archived to, and from where it can be restored.<br>" +
        "Click 'Next' to set up some destinations." +
        "</font>"
    }
}