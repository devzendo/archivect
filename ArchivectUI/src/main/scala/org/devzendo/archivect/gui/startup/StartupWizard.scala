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
import java.awt.Dimension
import java.util.Map
import javax.swing.JPanel

import org.devzendo.archivect.gui.ArchivectMainPanel
import org.devzendo.archivect.destinations.Destinations
import org.netbeans.api.wizard._
import org.netbeans.spi.wizard._
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer

import scala.collection.mutable.ArrayBuffer

import org.apache.log4j.Logger
import org.devzendo.archivect.gui.menu.ArchivectMenuIdentifiers
import org.devzendo.commonapp.gui.menu.MenuWiring
import org.devzendo.commonapp.gui.WindowGeometryStore
import org.devzendo.commoncode.resource.ResourceLoader

object StartupWizard {
    private val LOGGER = Logger.getLogger(classOf[StartupWizard])
}

class StartupWizard(val mainPanel: ArchivectMainPanel, val destinations: Destinations, val mainFrame: Frame) {

    def startWizardFrame(): Unit = {
        StartupWizard.LOGGER.info("Starting Startup Wizard")
        val wizardPages: Array[WizardPage] = getWizardPages;
        val producer: WizardResultProducer = new WizardResultProducer() {
            def cancel(settings: java.util.Map[_, _]): Boolean = {
                true
            }

            @throws(classOf[WizardException])
            def finish(settings: java.util.Map[_, _]): AnyRef = {
                return "the eventual result of the wizard"
            }
        }
        val receiver = new WizardResultReceiver() {
            def finished(wizardResult: AnyRef): Unit = {
            }
            def cancelled(settings: Map[_, _]): Unit = {
            }
        }
        val wizard: Wizard = WizardPage.createWizard(wizardPages, producer)
        wizard.addWizardObserver(new WizardObserver() {
            def stepsChanged(wizard: Wizard): Unit = {
                StartupWizard.LOGGER .info("steps changed")
            }
            def navigabilityChanged(wizard: Wizard): Unit = {
                StartupWizard.LOGGER .info("navigability changed")
                // TODO: call destEditor.stopEditing() when wizard closes.  
            }
            def selectionChanged(wizard: Wizard): Unit = {
                StartupWizard.LOGGER .info("selection changed")   
            }
        })
        val wizardPanel = new JPanel(new BorderLayout)
        mainPanel.addPanel("wizard", wizardPanel)
        WizardDisplayer.installInContainer(wizardPanel, BorderLayout.CENTER, wizard, null, null, receiver)
        mainPanel.switchToPanel("wizard")
    }

    private def getWizardPages(): Array[WizardPage] = {
        val pages = new ArrayBuffer[WizardPage]()
        if (destinations.size == 0) {
            // perhaps there's a better way of determining if this is the
            // first use, in order to show the IntroPanel?
            pages += new IntroPanel()
            pages += new DestinationsEditorPanel(destinations, mainFrame)
        }
        pages += new LaunchpadPanel()
        return pages.toArray
    }
}