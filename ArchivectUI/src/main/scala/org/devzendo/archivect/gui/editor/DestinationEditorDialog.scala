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

import java.awt.{BorderLayout, Color, CardLayout, Frame, GridLayout, FlowLayout, Insets}
import java.awt.event.{ItemEvent, ItemListener, KeyListener, KeyEvent}
import java.io.File

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
    val types = Array[Object](LocalPanelName, SmbPanelName)
}
class DestinationEditorDialog(val parentFrame: Frame, val inputDestination: Option[Destination]) extends JDialog(parentFrame, true) {
    // Editable fields:
    private var nameLabel: JTextArea = null
    private var localPath: JTextArea = null
    private var smbPath: JTextArea = null
    private var smbUser: JTextArea = null
    private var smbPassword: JTextArea = null
    private var smbServer: JTextArea = null
    private var smbShare: JTextArea = null
    private var validationProblems: JLabel = null
    private var okButton: JButton = null
    private var cancelButton: JButton = null
    private var testButton: JButton = null
    private var typeCombo: JComboBox = null
    
    initialiseDialog()

    def getDestination(): Option[Destination] = {
        return None
    }
    
    private def initialiseDialog() = {
        nameLabel = new JTextArea()
        localPath = new JTextArea()
        smbPath = new JTextArea()
        smbUser = new JTextArea()
        smbPassword = new JTextArea()
        smbServer = new JTextArea()
        smbShare = new JTextArea() 
        validationProblems = new JLabel()
        
        validateOnKey(nameLabel, localPath, smbPath, smbUser, smbPassword,
            smbServer, smbShare)

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
        topPanel.add(nameLabel, cc.xy(3, 1))
    
        topPanel.add(new JLabel("Type:"), cc.xy(1, 3))
        typeCombo = new JComboBox(DestinationEditorDialog.types)
        topPanel.add(typeCombo, cc.xy(3, 3))
    
        enclosingPanel.add(topPanel)

        // Card layout
        val cardLayout = new CardLayout()
        val cardPanel = new JPanel(cardLayout)
        cardPanel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLUE)) // TODO: remove diag
    
        // Local card
        val localFormLayout = new FormLayout(labelCol + ", 4dlu, " + fieldCol,    // columns
                                             "pref")    // rows
        val localRowGroups: Array[Array[Int]] = Array[Array[Int]](Array(1))
        localFormLayout.setRowGroups(localRowGroups)
        val lcc = new CellConstraints()

        val localPanel = new JPanel(localFormLayout)
        localPanel.add(new JLabel("Path:"), lcc.xy(1, 1))
        localPanel.add(localPath, lcc.xy(3, 1))
    
        cardPanel.add(localPanel, DestinationEditorDialog.LocalPanelName)
    
        // SMB card
        val smbFormLayout = new FormLayout(labelCol + ", 4dlu, " + fieldCol,    // columns
                                           "pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref")    // rows
        val smbRowGroups: Array[Array[Int]] = Array[Array[Int]](Array(1, 3, 5, 7, 9))
        smbFormLayout.setRowGroups(smbRowGroups)
        val scc = new CellConstraints()

        val smbPanel = new JPanel(smbFormLayout)
        smbPanel.add(new JLabel("Path:"), scc.xy(1, 1))
        smbPanel.add(smbPath, scc.xy(3, 1))
        smbPanel.add(new JLabel("User name:"), scc.xy(1, 3))
        smbPanel.add(smbUser, scc.xy(3, 3))
        smbPanel.add(new JLabel("Password:"), scc.xy(1, 5))
        smbPanel.add(smbPassword, scc.xy(3, 5))
        smbPanel.add(new JLabel("Server:"), scc.xy(1, 7))
        smbPanel.add(smbServer, scc.xy(3, 7))
        smbPanel.add(new JLabel("Share:"), scc.xy(1, 9))
        
        smbPanel.add(smbShare, scc.xy(3, 9))
    
        cardPanel.add(smbPanel, DestinationEditorDialog.SmbPanelName)

        enclosingPanel.add(cardPanel)
    
        inputDestination match {
            case None =>
                setTitle("Add destination")
            case Some(d) =>
                setTitle("Edit destination")
                nameLabel.setText(d.name)
                d match {
                    case l: LocalDestination =>
                        localPath.setText(l.localPath)
                        typeCombo.setSelectedItem(DestinationEditorDialog.types(0))
                    case s: SmbDestination =>
                        smbPassword.setText(s.password)
                        smbPath.setText(s.localPath)
                        smbServer.setText(s.server)
                        smbUser.setText(s.userName)
                        smbShare.setText(s.share)
                        typeCombo.setSelectedItem(DestinationEditorDialog.types(1))
                }
        }
    
        val valFormLayout = new FormLayout("fill:pref",        // columns
                                           "2dlu, pref, 8dlu") // rows
        val vcc = new CellConstraints()
        val validationPanel = new JPanel(valFormLayout)
        validationPanel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.GREEN)) // TODO: remove diag
        
        validationPanel.add(validationProblems, cc.xy(1, 2))
        enclosingPanel.add(validationPanel)
    
        // Button bar
        val builder = new ButtonBarBuilder2()
        testButton = new JButton("Test access")
        okButton = new JButton("OK")
        cancelButton = new JButton("Cancel")
        if (OSTypeDetect.getInstance().getOSType() == OSTypeDetect.OSType.MacOSX) {
            List(testButton, okButton, cancelButton) foreach (small(_))
        }
        builder.addButton(testButton)
        builder.addUnrelatedGap()
        builder.addGlue()
        getRootPane().setDefaultButton(okButton)
        builder.addButton(okButton, cancelButton)
        
        enclosingPanel.add(builder.getPanel())

        typeCombo.addItemListener((_ : ItemEvent) => {
            val selected = typeCombo.getSelectedItem()
            cardLayout.show(cardPanel, selected.toString())
            validateDialog()
        })

        validateDialog()
    }
    
    private def validateOnKey(fields: JTextArea*) = {
        for (field <- fields) {
            field.addKeyListener(new KeyListener() {
                def keyPressed(e: KeyEvent) = {
                }

                def keyReleased(e: KeyEvent) = {
                    validateDialog()
                }

                def keyTyped(e: KeyEvent) = {
                }
            })
        }
    }
    
    private def validateDialog() = {
        try {
            validateFields()
            ok()
        } catch {
            case ex: Exception =>  problem(ex.getMessage())
        }
    }
    
    private def validateFields() = {
        if (nameLabel.getText().trim.equals("")) {
            throw new RuntimeException("You must enter a name")
        }
        typeCombo.getSelectedItem() match {
            case DestinationEditorDialog.LocalPanelName => // local
                val localPathText = localPath.getText().trim()
                if (localPathText.equals("")) {
                    throw new RuntimeException("You must enter a local path")
                }
                val pathFile = new File(localPathText)
                if (!pathFile.exists) {
                    throw new RuntimeException(localPathText + " does not exist")
                }
                if (!pathFile.isDirectory) {
                    throw new RuntimeException(localPathText + " is not a directory")
                }
            
            case DestinationEditorDialog.SmbPanelName  => // smb
                val smbPathText = smbPath.getText().trim()
                if (smbPathText.equals("")) {
                    throw new RuntimeException("You must enter a SMB path")
                }
                val pathFile = new File(smbPathText)
                if (!pathFile.exists) {
                    throw new RuntimeException(smbPathText + " does not exist")
                }
                if (!pathFile.isDirectory) {
                    throw new RuntimeException(smbPathText + " is not a directory")
                }
        }
    }
    
    private def problem(message: String) = {
        validationProblems.setForeground(Color.RED)
        validationProblems.setText(message)
        okButton.setEnabled(false)
        testButton.setEnabled(false)
    }
    
    private def ok() = {
        validationProblems.setText(" ")
        okButton.setEnabled(true)
        testButton.setEnabled(true)
    }
    
    private def small(button: JButton) = {
        button.putClientProperty("JComponent.sizeVariant", "small")
    }

    // just to get a decent border...
    override def getInsets(): Insets = {
        new Insets(40, 20, 20, 20)
    }
}