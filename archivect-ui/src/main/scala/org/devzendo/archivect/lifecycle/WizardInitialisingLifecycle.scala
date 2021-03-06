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
 
package org.devzendo.archivect.lifecycle

import javax.swing.UIManager

import org.devzendo.archivect.gui.startup.StartupWizard
import org.devzendo.commonapp.lifecycle.Lifecycle
import org.devzendo.commonapp.gui.GUIUtils
import org.devzendo.commoncode.resource.ResourceLoader

import org.apache.log4j.Logger
import org.devzendo.archivect.gui.menu.ArchivectMenuIdentifiers
import org.devzendo.commonapp.gui.menu.MenuWiring
import org.devzendo.commonapp.gui.WindowGeometryStore
import org.devzendo.commoncode.resource.ResourceLoader

object WizardInitialisingLifecycle {
    private val LOGGER = Logger.getLogger(classOf[WizardInitialisingLifecycle])
}

class WizardInitialisingLifecycle() extends Lifecycle {

    def startup(): Unit = {
        GUIUtils.runOnEventThread(new Runnable() {
            def run = {
                loadLeftHandGraphic()
            }
        })
    }
    
    def shutdown(): Unit = {
        
    }
    
    private def loadLeftHandGraphic() = {
        val image = ResourceLoader.readImageResource(
            "org/devzendo/archivect/graphics/WizardLeftHandPanel.png");
        if (image != null) {
            UIManager.put("wizard.sidebar.image", image);
        }
    }
}