package org.devzendo.archivect.gui

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JSeparator

import org.apache.log4j.Logger

import org.devzendo.archivect.gui.SwingImplicits._
import org.devzendo.commonapp.gui.menu.AbstractRebuildableMenuGroup
import org.devzendo.commonapp.gui.menu.MenuWiring
import org.devzendo.archivect.gui.recent.RecentJobsList

object FileMenu {
    private val LOGGER = Logger.getLogger(classOf[FileMenu])
}

class FileMenu(val menuWiring: MenuWiring, val recentList: RecentJobsList) extends AbstractRebuildableMenuGroup(menuWiring) {
    private var fileMenu: JMenu = new JMenu("File")
    fileMenu.setMnemonic('F')
    // Trigger the first build; initially we'll have no recent files list.
    // Need to do an initial rebuild so the menu wiring is initially populated
    rebuildMenuGroup()
    
    def getJMenu: JMenu = {
        fileMenu
    }
    
    def rebuildMenuGroup() = {
        fileMenu.removeAll();

        createMenuItem(ArchivectMenuIdentifiers.FILE_NEW, "New...", 'N', fileMenu)
        createMenuItem(ArchivectMenuIdentifiers.FILE_OPEN, "Open...", 'O', fileMenu)
        fileMenu.add(buildRecentList())

        fileMenu.add(new JSeparator())

        createMenuItem(ArchivectMenuIdentifiers.FILE_CLOSE, "Close", 'C', fileMenu)

        fileMenu.add(new JSeparator())

        createMenuItem(ArchivectMenuIdentifiers.FILE_EXIT, "Exit", 'x', fileMenu)
    }
    
    private def buildRecentList(): JMenu = {
        val submenu = new JMenu("Open recent")
        submenu.setMnemonic('r')
        val numberOfRecentFiles = recentList.getNumberOfEntries
        if (numberOfRecentFiles == 0) {
            submenu.setEnabled(false)
        } else {
            val recentJobs: List[String] = recentList.getRecentJobs
            var mnemonic = 1
            for (job <- recentJobs) {
                val menuItem = new JMenuItem("" + mnemonic + " " + job)
                menuItem.setMnemonic(KeyEvent.VK_0 + mnemonic)
                menuItem.addActionListener((_ : ActionEvent) => {
                    FileMenu.LOGGER.info("Implicitly triggered recent menu " + job)
                })
                mnemonic = mnemonic + 1
/*
                        new Thread(new Runnable() {
                            def run = {
                                try {
                                    Thread.currentThread().setName("RecentOpener:" + job)
                                    Thread.currentThread().setPriority(Thread.MIN_PRIORITY + 1)
                                    FileMenu.LOGGER.info("Opening recent job '" + job + "'")
                                    //openRecentSubmenuChoiceObservers.eventOccurred(
                                    //        new DatabaseNameAndPathChoice(recentDbName, recentDbPath));
                                } catch (final Throwable t) {
                                    FileMenu.LOGGER.error("Recent opener thread caught unexpected " + t.getClass().getSimpleName(), t)
                                } finally {
                                    FileMenu.LOGGER.debug("Open recent complete")
                                }
                            }
                        }).start()
*/                        
                    //}
                //})
                submenu.add(menuItem)
            }
            // enabled by default
        }
        submenu
    }

}