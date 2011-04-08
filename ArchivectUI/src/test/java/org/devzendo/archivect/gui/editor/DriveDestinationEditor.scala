package org.devzendo.archivect.gui.editor

import collection.JavaConversions._

import java.awt.{BorderLayout, FlowLayout}
import java.awt.event.{ActionListener, ActionEvent, WindowAdapter, WindowEvent}
import javax.swing.{JLabel, JFrame, JButton, JPanel, WindowConstants}

import org.apache.log4j.Logger

import org.devzendo.commonapp.prefs._

import org.devzendo.commonapp.gui.Beautifier
import org.devzendo.commonapp.gui.GUIUtils
import org.devzendo.commonapp.gui.ThreadCheckingRepaintManager
import org.devzendo.commoncode.logging.Logging
import org.devzendo.commoncode.patterns.observer.{Observer, ObserverList}

import org.devzendo.archivect.destinations._
import org.devzendo.archivect.gui.SwingImplicits._

object DriveDestinationEditor {
    private val LOGGER = Logger.getLogger(classOf[DestinationEditor])
    // not sure why this can't be this class?!
    
    def main(args: Array[String]): Unit = {
        val logging = Logging.getInstance()
        logging.setupLoggingFromArgs(args.toList)

        GUIUtils.runOnEventThread(new Runnable() {
            def run = {
                ThreadCheckingRepaintManager.initialise();
                Beautifier.makeBeautiful();
                DriveDestinationEditor.LOGGER.info("Starting driver")

                val destinations = loadDestinations()
                
                val frame = new JFrame("Drive Destinations Editor")
                frame.setLayout(new BorderLayout())
                
                val messageLabel = new JLabel(" ")
                frame.add(messageLabel, BorderLayout.SOUTH)
                
                val destEditor = new DestinationEditor(destinations, frame)
                frame.add(destEditor, BorderLayout.CENTER)
                
                destEditor.addDestinationListener(new Observer[DestinationEditorEvent]() {
                    def eventOccurred(observableEvent: DestinationEditorEvent): Unit = {
                        if (destinations.size == 0) {
                            messageLabel.setText("You must create one or more destinations")
                        } else {
                            messageLabel.setText(" ")
                        }
                    }
                })
                destEditor.startEditing() // trigger empty message
    
    
                frame.addWindowListener(new WindowAdapter() {
                    override def windowClosing(e: WindowEvent) {
                        DriveDestinationEditor.LOGGER.info("Detected window closing; removing listeners")
                        destEditor.stopEditing()
                        frame.dispose()
                    }
                    override def windowClosed(e: WindowEvent ) {
                        DriveDestinationEditor.LOGGER.info("Detected window closed")
                }})
    
                frame.pack()
                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
                frame.setVisible(true)
            }
        })
    }

    def loadDestinations(): Destinations = {
        val prefsLocation = new DefaultPrefsLocation(".archivect", "archivect.prefs")
        new DestinationsFactory(prefsLocation, "drivedestinationseditor.xml").getObject()        
    }
}
