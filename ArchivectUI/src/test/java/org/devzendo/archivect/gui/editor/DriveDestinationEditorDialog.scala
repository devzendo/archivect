package org.devzendo.archivect.gui.editor

import collection.JavaConversions._

import org.devzendo.commonapp.gui.Beautifier
import org.devzendo.commonapp.gui.GUIUtils
import org.devzendo.commonapp.gui.ThreadCheckingRepaintManager
import org.devzendo.commoncode.logging.Logging

import java.awt.{ BorderLayout, FlowLayout }
import java.awt.event.{ ActionListener, ActionEvent }
import javax.swing.{ JLabel, JFrame, JButton, JPanel }
import org.devzendo.archivect.destinations._
//Destinations.{ Destination, LocalDestination, SmbDestination }
import org.devzendo.archivect.gui.SwingImplicits._

object DriveDestinationEditorDialog {
    def main(args: Array[String]): Unit = {
        val logging = Logging.getInstance()
        logging.setupLoggingFromArgs(args.toList)

        GUIUtils.runOnEventThread(new Runnable() {
            def run = {
                ThreadCheckingRepaintManager.initialise();
                Beautifier.makeBeautiful();

                val frame = new JFrame("Test")
                frame.setLayout(new BorderLayout())
                val returnLabel = new JLabel("return value")
                frame.add(returnLabel, BorderLayout.SOUTH)

                val buttonPanel = new JPanel(new FlowLayout())
                val localEditButton = new JButton("Edit Local Destination")
                localEditButton.addActionListener((_ :ActionEvent) => {
                    show(returnLabel, edit(frame, new Some[Destination](LocalDestination("My Local", "/tmp/local"))))
                })
                val smbEditButton = new JButton("Edit SMB Destination")
                smbEditButton.addActionListener((_ :ActionEvent) => {
                    show(returnLabel, edit(frame, new Some[Destination](SmbDestination("My SMB", "server",
                        "publicshare", "JoeBloggs", "password", "/tmp/share"))))
                })
                val addButton = new JButton("Add new Destination")
                addButton.addActionListener((_ :ActionEvent) => {
                    show(returnLabel, edit(frame, None))
                })
                buttonPanel.add(localEditButton)
                buttonPanel.add(smbEditButton)
                buttonPanel.add(addButton)
                frame.add(buttonPanel, BorderLayout.NORTH)
                
                frame.pack()
                frame.setVisible(true)

            }
        })
    }
    def show(returnLabel: JLabel, dest: Option[Destination]) = {
        dest match {
            case None => returnLabel.setText("none returned")
            case Some(d) => returnLabel.setText("[" + d.toString() + "]")
        }
    }

    def edit(frame: JFrame, dest: Option[Destination]): Option[Destination] = {
        val dialog = new DestinationEditorDialog(frame, dest)
        dialog.pack()
        dialog.setVisible(true) // blocks until disposed 
        return dialog.getDestination()
    }

}
