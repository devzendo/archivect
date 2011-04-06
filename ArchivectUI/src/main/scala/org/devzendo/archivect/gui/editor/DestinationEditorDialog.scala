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

package org.devzendo.archivect.gui.editor

import java.awt.{BorderLayout, CardLayout, Frame, GridLayout, FlowLayout, Insets}
import java.awt.event.{ItemEvent, ItemListener}


import javax.swing.{Box, BoxLayout, JLabel, JTextArea, JComboBox, JButton,
    WindowConstants, SwingConstants, JDialog, JPanel, JSeparator}

import com.jgoodies.forms.builder.{ButtonBarBuilder2}
import com.jgoodies.forms.layout.{FormLayout, CellConstraints}

import org.devzendo.archivect.destinations._
import org.devzendo.archivect.gui.SwingImplicits._
import org.devzendo.commonapp.gui.GUIUtils
import org.devzendo.commoncode.os.OSTypeDetect
import org.devzendo.commoncode.os.OSTypeDetect._

object DestinationEditorDialog {
    val LocalPanelName = "Local Disk"
    val SmbPanelName = "Windows Share"
}
class DestinationEditorDialog(val parentFrame: Frame, val inputDestination: Option[Destination]) extends JDialog(parentFrame, true) {
    // Handle window closing correctly.
    //setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)

    val labelCol = "right:50dlu"
    val fieldCol = "fill:100dlu"

    val cc = new CellConstraints()
    val topFormLayout = new FormLayout(labelCol + ", 4dlu, " + fieldCol,    // columns
                                       "pref, 2dlu, pref, 2dlu, pref")    // rows
    val rowGroups: Array[Array[Int]] = Array[Array[Int]](Array(1, 3, 5))
    topFormLayout.setRowGroups(rowGroups)
    
    val enclosingPanel = new JPanel()
    enclosingPanel.setLayout(new BoxLayout(enclosingPanel, BoxLayout.Y_AXIS))
    add(enclosingPanel)

    // Top panel
    val topPanel = new JPanel(topFormLayout)
    topPanel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.RED)) // TODO: remove diag

    topPanel.add(new JLabel("Name:"), cc.xy(1, 1))
    val nameLabel = new JTextArea()
    topPanel.add(nameLabel, cc.xy(3, 1))
    
    topPanel.add(new JLabel("Type:"), cc.xy(1, 3))
    private val types = Array[Object](DestinationEditorDialog.LocalPanelName, DestinationEditorDialog.SmbPanelName)
    private val typeCombo = new JComboBox(types)
    topPanel.add(typeCombo, cc.xy(3, 3))
    
    enclosingPanel.add(topPanel)

    // Card layout
    private val cardLayout = new CardLayout()
    private val cardPanel = new JPanel(cardLayout)
    cardPanel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLUE)) // TODO: remove diag
    
    // Local card
    val localFormLayout = new FormLayout(labelCol + ", 4dlu, " + fieldCol,    // columns
                                         "pref")    // rows
    val localRowGroups: Array[Array[Int]] = Array[Array[Int]](Array(1))
    localFormLayout.setRowGroups(localRowGroups)
    val lcc = new CellConstraints()

    private val localPanel = new JPanel(localFormLayout)
    localPanel.add(new JLabel("Path:"), lcc.xy(1, 1))
    private val localPath = new JTextArea()
    localPanel.add(localPath, lcc.xy(3, 1))
    
    cardPanel.add(localPanel, DestinationEditorDialog.LocalPanelName)
    
    // SMB card
    val smbFormLayout = new FormLayout(labelCol + ", 4dlu, " + fieldCol,    // columns
                                       "pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref")    // rows
    val smbRowGroups: Array[Array[Int]] = Array[Array[Int]](Array(1, 3, 5, 7, 9))
    smbFormLayout.setRowGroups(smbRowGroups)
    val scc = new CellConstraints()

    private val smbPanel = new JPanel(smbFormLayout)
    smbPanel.add(new JLabel("Path:"), scc.xy(1, 1))
    private val smbPath = new JTextArea()
    smbPanel.add(smbPath, scc.xy(3, 1))
    smbPanel.add(new JLabel("User name:"), scc.xy(1, 3))
    private val smbUser = new JTextArea()
    smbPanel.add(smbUser, scc.xy(3, 3))
    smbPanel.add(new JLabel("Password:"), scc.xy(1, 5))
    private val smbPassword = new JTextArea()
    smbPanel.add(smbPassword, scc.xy(3, 5))
    smbPanel.add(new JLabel("Server:"), scc.xy(1, 7))
    private val smbServer = new JTextArea()
    smbPanel.add(smbServer, scc.xy(3, 7))
    smbPanel.add(new JLabel("Share:"), scc.xy(1, 9))
    private val smbShare = new JTextArea()
    smbPanel.add(smbShare, scc.xy(3, 9))
    
    cardPanel.add(smbPanel, DestinationEditorDialog.SmbPanelName)

    enclosingPanel.add(cardPanel)
    
    typeCombo.addItemListener((_ : ItemEvent) => {
        val selected = typeCombo.getSelectedItem()
        cardLayout.show(cardPanel, selected.toString())
    })

    inputDestination match {
        case None =>
            setTitle("Add destination")
        case Some(d) =>
            setTitle("Edit destination")
            d match {
                case l: LocalDestination =>
                    typeCombo.setSelectedItem(types(0))
                case s: SmbDestination =>
                    typeCombo.setSelectedItem(types(1))
            }
    }

    private val valFormLayout = new FormLayout("fill:pref",        // columns
                                               "2dlu, pref, 8dlu") // rows
    val vcc = new CellConstraints()
    private val validationPanel = new JPanel(valFormLayout)
    validationPanel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.GREEN)) // TODO: remove diag
    private val validationProblems = GUIUtils.createNonEditableJTextAreaWithParentBackground(validationPanel)
    validationProblems.setText("** something **");
    validationPanel.add(validationProblems, cc.xy(1, 2))
    enclosingPanel.add(validationPanel)

    // Button bar
    val builder = new ButtonBarBuilder2()
    private val testButton = new JButton("Test access")
    private val okButton = new JButton("OK")
    private val cancelButton = new JButton("Cancel")
    if (OSTypeDetect.getInstance().getOSType() == OSTypeDetect.OSType.MacOSX) {
        List(testButton, okButton, cancelButton) foreach (small(_))
    }
    builder.addButton(testButton)
    builder.addUnrelatedGap()
    builder.addGlue()
    //okButton.setDefaultCapable(true)
    getRootPane().setDefaultButton(okButton)
    builder.addButton(okButton, cancelButton)
    
    enclosingPanel.add(builder.getPanel())
    
    def getDestination(): Option[Destination] = {
        return None
    }
    
    private def small(button: JButton) = {
        button.putClientProperty("JComponent.sizeVariant", "small")
    }

    // just to get a decent border...
    override def getInsets(): Insets = {
        new Insets(40, 20, 20, 20)
    }
}