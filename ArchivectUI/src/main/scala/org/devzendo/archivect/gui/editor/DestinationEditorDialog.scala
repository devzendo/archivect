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
import java.awt.event.{ActionEvent, ActionListener,
    ItemEvent, ItemListener,
    KeyEvent, KeyListener}
import java.io.File

import javax.swing.{Box, BoxLayout,
    JComponent,
    JLabel, JTextField, JPasswordField, JCheckBox,
    JComboBox, JButton, JDialog, JPanel, JSeparator,
    WindowConstants, SwingConstants }

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
    private var nameLabel: JTextField = null
    private var localPath: JTextField = null
    private var smbPath: JTextField = null
    private var smbUser: JTextField = null
    private var smbPassword: JPasswordField = null
    private var echoChar = '\0'
    private var smbServer: JTextField = null
    private var smbShare: JTextField = null
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
        nameLabel = new JTextField()
        localPath = new JTextField()
        smbPath = new JTextField()
        smbUser = new JTextField()
        smbPassword = new JPasswordField()
        
        echoChar = smbPassword.getEchoChar()
        smbServer = new JTextField()
        smbShare = new JTextField() 
        validationProblems = new JLabel()
        
        var smbShowPassword = new JCheckBox("Show password")
        
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
                                           "pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref")    // rows
        val scc = new CellConstraints()

        val smbPanel = new JPanel(smbFormLayout)
        smbPanel.add(new JLabel("Path:"), scc.xy(1, 1))
        smbPanel.add(smbPath, scc.xy(3, 1))
        smbPanel.add(new JLabel("User name:"), scc.xy(1, 3))
        smbPanel.add(smbUser, scc.xy(3, 3))
        smbPanel.add(new JLabel("Password:"), scc.xy(1, 5))
        smbPanel.add(smbPassword, scc.xy(3, 5))
        smbPanel.add(smbShowPassword, scc.xy(3, 7))
        smbPanel.add(new JLabel("Server:"), scc.xy(1, 9))
        smbPanel.add(smbServer, scc.xy(3, 9))
        smbPanel.add(new JLabel("Share:"), scc.xy(1, 11))
        smbPanel.add(smbShare, scc.xy(3, 11))
    
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
                nameLabel.setText(d.name)
                d match {
                    case l: LocalDestination =>
                        localPath.setText(l.localPath)
                        typeCombo.setSelectedItem(DestinationEditorDialog.LocalPanelName)
                    case s: SmbDestination =>
                        smbPassword.setText(s.password)
                        smbPath.setText(s.localPath)
                        smbServer.setText(s.server)
                        smbUser.setText(s.userName)
                        smbShare.setText(s.share)
                        typeCombo.setSelectedItem(DestinationEditorDialog.SmbPanelName)
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
            small(smbShowPassword)
        }
        builder.addButton(testButton)
        builder.addUnrelatedGap()
        builder.addGlue()
        getRootPane().setDefaultButton(okButton)
        builder.addButton(okButton, cancelButton)

        enclosingPanel.add(builder.getPanel())

        smbShowPassword.addActionListener((_ : ActionEvent) => {
            smbPassword.setEchoChar(if (smbPassword.getEchoChar() == 0) echoChar else '\0')
        })

        typeCombo.addItemListener((_ : ItemEvent) => {
            validateDialog()
        })

        validateDialog()
    }
    
    private def validateOnKey(fields: JTextField*) = {
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
        fieldMustBeNonEmpty(nameLabel, "name")
        typeCombo.getSelectedItem() match {
            case DestinationEditorDialog.LocalPanelName =>
                val localPathText = localPath.getText().trim()
                validatePath(localPathText)
            
            case DestinationEditorDialog.SmbPanelName  =>
                val smbPathText = smbPath.getText().trim()
                validatePath(smbPathText)
                fieldMustBeNonEmpty(smbUser, "user name")
                fieldMustBeNonEmpty(smbPassword, "password")
                fieldMustBeNonEmpty(smbServer, "server name")
                fieldMustBeNonEmpty(smbShare, "share name")
        }
    }

    private def fieldMustBeNonEmpty(field: JTextField, description: String) = {
        if (field.getText().trim.equals("")) {
            throw new RuntimeException("You must enter a " + description)
        }
    }
    
    private def validatePath(path: String) = {
        if (path.equals("")) {
            throw new RuntimeException("You must enter a path")
        }
        val pathFile = new File(path)
        if (!pathFile.exists) {
            throw new RuntimeException(path + " does not exist")
        }
        if (!pathFile.isDirectory) {
            throw new RuntimeException(path + " is not a directory")
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
    
    private def small(component: JComponent) = {
        component.putClientProperty("JComponent.sizeVariant", "small")
    }

    // just to get a decent border...
    override def getInsets(): Insets = {
        new Insets(40, 20, 20, 20)
    }
}