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

import java.awt.{BorderLayout, Frame}
import javax.swing.{JPanel}

import org.devzendo.archivect.destinations.Destinations
import org.devzendo.archivect.gui.editor.{DestinationEditor, DestinationEditorEvent}

import org.devzendo.commoncode.patterns.observer.{Observer, ObserverList}

import org.netbeans.spi.wizard.{WizardPage, WizardController}

object DestinationsEditorPanel {
    def getDescription = {
        "Edit Destinations" 
    }
}
class DestinationsEditorPanel(val destinations: Destinations, val mainFrame: Frame) extends WizardPage {
    setLayout(new BorderLayout())
    val destEditor = new DestinationEditor(destinations, mainFrame)
    add(destEditor, BorderLayout.CENTER)

    destEditor.addDestinationListener(new Observer[DestinationEditorEvent]() {
        def eventOccurred(observableEvent: DestinationEditorEvent): Unit = {
            validateDestinationCount()
        }
    })
    destEditor.startEditing() // trigger empty message
    
    // TODO: call destEditor.stopEditing() when wizard closes.
    //validateDestinationCount()
    
    private def validateDestinationCount() = {
        setForwardNavigationMode(WizardController.MODE_CAN_CONTINUE)
        if (destinations.size == 0) {
            setProblem("You must create one or more destinations")
        } else {
            setProblem(null)
        }
    }
    
}