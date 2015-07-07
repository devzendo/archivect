package org.devzendo.archivect.destinations

import java.io.File
import org.springframework.beans.factory.FactoryBean
import org.devzendo.commonapp.prefs.PrefsLocation

class DestinationsFactory(val prefsLocation: PrefsLocation, val destinationsFileName: String) extends FactoryBean[Destinations] {

  def getObject(): Destinations = {
      val destinationsFile = new File(
              prefsLocation.getPrefsDir,
              destinationsFileName).getAbsolutePath()
      new DefaultDestinations(destinationsFile)
  }

  def getObjectType(): java.lang.Class[_] = { classOf[Destinations] }

  def isSingleton(): Boolean = { true }
}