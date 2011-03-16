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
import javax.swing.{JPanel}

import org.devzendo.archivect.destinations.Destinations
import org.devzendo.archivect.gui.editor.DestinationEditor

import org.netbeans.spi.wizard.WizardPage

object DestinationsEditorPanel {
    def getDescription = {
        "Edit Destinations" 
    }
}
class DestinationsEditorPanel(val destinations: Destinations) extends WizardPage {
    setLayout(new BorderLayout())
    val destEditPanel = new DestinationEditor(destinations)
    add(destEditPanel, BorderLayout.CENTER)
}