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

import java.awt.{BorderLayout, Frame}
import java.awt.event.ActionEvent
import javax.swing.{BoxLayout, JPanel, JTable, JButton, JScrollPane}
import javax.swing.event.{ListSelectionEvent}
import javax.swing.table.AbstractTableModel

import com.nadeausoftware.ZebraJTable
import org.apache.log4j.Logger

import org.devzendo.archivect.destinations.{Destinations, DestinationEvent}
import org.devzendo.archivect.gui.SwingImplicits._
import org.devzendo.commoncode.patterns.observer.{Observer, ObserverList, ObservableEvent}

sealed abstract class DestinationEditorEvent extends ObservableEvent
case class DestinationEmptyEvent extends DestinationEditorEvent
case class DestinationAddedEvent extends DestinationEditorEvent
case class DestinationRemovedEvent extends DestinationEditorEvent
 
object DestinationEditor {
    private val LOGGER = Logger.getLogger(classOf[DestinationEditor])
}

class DestinationEditor(val destinations: Destinations, val mainFrame: Frame) extends JPanel {
    private val listeners = new ObserverList[DestinationEditorEvent]
    private val dataModel = new DestinationsSummaryTableModel()
    private val table = new ZebraJTable(dataModel)
    table.setRowSelectionAllowed(true)
    table.getSelectionModel().addListSelectionListener(tableSelectionListener)
    
    setLayout(new BorderLayout())
    private val buttonPanel = new JPanel()
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS))
    private val addButton = new JButton("Add...")
    addButton.addActionListener(addActionListener)
    
    private val removeButton = new JButton("Remove")
    removeButton.addActionListener(removeActionListener)
    
    private val editButton = new JButton("Edit")
    buttonPanel.add(addButton)
    buttonPanel.add(removeButton)
    buttonPanel.add(editButton)
    add(buttonPanel, BorderLayout.EAST)
    add(new JScrollPane(table), BorderLayout.CENTER);
//    dataModel.fireTableStructureChanged()
    enableButtons
    val buttonEnablingListener = new Observer[DestinationEvent]() {
        def eventOccurred(event: DestinationEvent) = {
            DestinationEditor.this.enableButtons
        }
    }
    destinations.addDestinationListener(buttonEnablingListener)
    
    private def enableButtons = {
        val empty = destinations.size == 0
        val rowSelected = table.getSelectedRow() != -1
        addButton.setEnabled(true)
        removeButton.setEnabled(!empty && rowSelected)
        editButton.setEnabled(!empty)
    }
    
    private def addActionListener() = (_ : ActionEvent) => {
        val dialog = new DestinationEditorDialog(mainFrame, None)
        dialog.pack()
        dialog.setLocationRelativeTo(this)
        dialog.setVisible(true) // blocks until closed
        val newDestination = dialog.getDestination()
        newDestination match {
            case None => // do nothing, they cancelled
            case Some(d) =>
                // TODO: duplication check? The dialog should prevent a dupe.
                destinations.addDestination(d) 
        }            
        DestinationEditor.LOGGER.info("Add")
        // not sure why I don't need a dataModel.fireTableStructureChanged() here
    }
    
    private def removeActionListener() = (_ : ActionEvent) => {
        val selectedRow = table.getSelectedRow()
        val destinationToRemove = destinations.getDestination(selectedRow)
        DestinationEditor.LOGGER.info("Removing " + destinationToRemove)
        destinations.removeDestination(destinationToRemove)
        dataModel.fireTableStructureChanged()
    }

    private def tableSelectionListener() = (e : ListSelectionEvent) => {
        if (!e.getValueIsAdjusting()) {
            enableButtons
            DestinationEditor.LOGGER.info("Table selection: " + e)
        }
    }
    
    
    private class DestinationsSummaryTableModel() extends AbstractTableModel {
        override def isCellEditable(row: Int, col: Int): Boolean = {
            false
        }
        
        override def getColumnName(col: Int): String = {
            col match {
                case 0 => "Name"
                case 1 => "Type"
            }
        }

        def getColumnCount = { 2 }
        
        def getRowCount = { DestinationEditor.this.destinations.size }
        
        def getValueAt(row: Int, col: Int): Object = {
            val dests = DestinationEditor.this.destinations
            if (col >= 2 || row >= dests.size) {
                return "";
            }
            val summary = dests.summaries(row)
            col match {
                case 0 => summary.name
                case 1 => summary.destinationType
            }
        }
    }
    
    def startEditing(): Unit = {
        if (destinations.size == 0) {
            listeners.eventOccurred(new DestinationEmptyEvent())
        }
    }
        
    def stopEditing(): Unit = {
        destinations.removeDestinationListener(buttonEnablingListener)
    }
    
    def addDestinationListener(listener: Observer[DestinationEditorEvent]) = {
        listeners.addObserver(listener)
    }
    
    def removeDestinationListener(listener: Observer[DestinationEditorEvent]) = {
        listeners.removeListener(listener)
    }
}