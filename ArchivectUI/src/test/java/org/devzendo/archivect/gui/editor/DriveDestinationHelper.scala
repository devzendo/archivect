package org.devzendo.archivect.gui.editor

import org.devzendo.commonapp.prefs._

import org.devzendo.archivect.destinations._

object DriveDestinationHelper {

    def loadDestinations(): Destinations = {
        val prefsLocation = new DefaultPrefsLocation(".archivect", "archivect.prefs")
        new DestinationsFactory(prefsLocation, "drivedestinationseditor.xml").getObject()        
    }

}