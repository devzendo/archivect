package org.devzendo.archivect.gui.editor

import collection.JavaConversions._

import org.devzendo.commonapp.gui.Beautifier
import org.devzendo.commonapp.gui.GUIUtils
import org.devzendo.commonapp.gui.ThreadCheckingRepaintManager
import org.devzendo.commoncode.logging.Logging

import javax.swing.{ JLabel, JFrame, WindowConstants }

object DriveDestinationEditorDialog {
    def main(args: Array[String]): Unit = {
        val logging = Logging.getInstance()
        logging.setupLoggingFromArgs(args.toList)

        GUIUtils.runOnEventThread(new Runnable() {
            def run = {
                ThreadCheckingRepaintManager.initialise();
                Beautifier.makeBeautiful();

                val dialog = new DestinationEditorDialog(null)
                dialog.pack()
                dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
                dialog.setVisible(true)
            }
        })

    }
}
