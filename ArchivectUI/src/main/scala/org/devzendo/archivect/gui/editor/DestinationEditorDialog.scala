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

import java.awt.{BorderLayout, CardLayout, Frame, GridLayout, FlowLayout}
import java.awt.event.{ItemEvent, ItemListener}


import javax.swing.{Box, BoxLayout, JLabel, JTextArea, JComboBox, JButton,
    WindowConstants, SwingConstants, JDialog, JPanel, JSeparator}

import com.jgoodies.forms.layout.{FormLayout, CellConstraints}

import org.devzendo.archivect.gui.SwingImplicits._
import org.devzendo.commonapp.gui.GUIUtils

object DestinationEditorDialog {
    val LocalPanelName = "Local"
    val SmbPanelName = "Smb"
}
class DestinationEditorDialog(val parentFrame: Frame) extends JDialog(parentFrame, true) {
    setTitle("Add destination") // TODO: change to edit if editing
    // Handle window closing correctly.
    //setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)

    val formLayout = new FormLayout("pref, 4dlu, 50dlu",    // columns
                                    "pref, 2dlu, pref, 2dlu, pref")    // rows
    val rowGroups: Array[Array[Int]] = Array[Array[Int]](Array(1, 3, 5))
    formLayout.setRowGroups(rowGroups)
    
    val enclosingPanel = new JPanel()
    setLayout(new BoxLayout(enclosingPanel, BoxLayout.Y_AXIS))
    add(enclosingPanel, BorderLayout.CENTER)

    val cc = new CellConstraints()
    val topPanel = new JPanel(formLayout)

    topPanel.add(new JLabel("Name:"), cc.xy(1, 1))
    val nameLabel = new JTextArea()
    topPanel.add(nameLabel, cc.xyw(3, 1, 3))
    
    topPanel.add(new JLabel("Type:"), cc.xy(1, 3))
    private val types = Array[Object]("Local Disk", "Windows Share")
    private val type2Panel = Map.empty[Object, JPanel]
    private val typeCombo = new JComboBox(types)
    topPanel.add(typeCombo, cc.xyw(3, 3, 3))
    
    enclosingPanel.add(topPanel)

    private val cardLayout = new CardLayout()
    private val cardPanel = new JPanel(cardLayout)
    enclosingPanel.add(cardPanel)
    
    private val localPanel = new JPanel(gridLayout())
    localPanel.add(new JLabel("Path:"))
    private val localPath = new JTextArea()
    localPanel.add(localPath)
    
    cardPanel.add(localPanel, DestinationEditorDialog.LocalPanelName)
    
    private val smbPanel = new JPanel(gridLayout())
    smbPanel.add(new JLabel("Path:"))
    private val smbPath = new JTextArea()
    smbPanel.add(smbPath)
    smbPanel.add(new JLabel("User name:"))
    private val smbUser = new JTextArea()
    smbPanel.add(smbUser)
    smbPanel.add(new JLabel("Password:"))
    private val smbPassword = new JTextArea()
    smbPanel.add(smbPassword)
    smbPanel.add(new JLabel("Server:"))
    private val smbServer = new JTextArea()
    smbPanel.add(smbServer)
    smbPanel.add(new JLabel("Share:"))
    private val smbShare = new JTextArea()
    smbPanel.add(smbShare)
    
    cardPanel.add(smbPanel, DestinationEditorDialog.SmbPanelName)
    
    typeCombo.addItemListener((_ : ItemEvent) => {
        val selected = typeCombo.getSelectedItem()
        cardLayout.show(type2Panel(selected), selected.toString())
    })
    
    private val validationPanel = new JPanel(new FlowLayout())
    private val validationProblems = GUIUtils.createNonEditableJTextAreaWithParentBackground(validationPanel)
    validationProblems.setText("  ");
    enclosingPanel.add(validationPanel)
    
    enclosingPanel.add(new JSeparator(SwingConstants.HORIZONTAL))
    
    private val buttonBox = new Box(BoxLayout.X_AXIS)
    private val testButton = new JButton("Test access")
    buttonBox.add(testButton)
    buttonBox.add(Box.createHorizontalStrut(20))
    private val okButton = new JButton("OK")
    buttonBox.add(okButton)
    private val cancelButton = new JButton("Cancel")
    buttonBox.add(cancelButton)
    
    enclosingPanel.add(buttonBox)
    
    private def gridLayout(): GridLayout = new GridLayout(4, 2, 16, 16)
}