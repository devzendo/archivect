package org.devzendo.archivect.gui.recent

class RecentJobsList {
    def getNumberOfEntries: Int = {
        3
    }
    def getRecentJobs: List[String] = {
        return List("One", "Two", "Three")
    }
}