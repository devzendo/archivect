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
package org.devzendo.archivect.prefs

import org.apache.log4j.Logger
import org.devzendo.commonapp.prefs.Prefs
import org.devzendo.commoncode.file.INIFile

/**
 * The .ini-file based implementation of ArchivectPrefs.
 * 
 * @author matt
 *
 */
object DefaultArchivectPrefs {
    private val LOGGER = Logger.getLogger(classOf[DefaultArchivectPrefs])
    // The section names and preference items
    private val SECTION_UI = "ui"
    private val UI_GEOMETRY = "geometry"

}
/**
 * @param String the path at which the .ini file should be stored.
 *
 */
class DefaultArchivectPrefs(prefsFilePath: String) extends ArchivectPrefs {

    DefaultArchivectPrefs.LOGGER.debug("Archivect preferences are stored at " + prefsFilePath)
    val iniFile = new INIFile(prefsFilePath)
    
    def getWindowGeometry(windowName: String): String = {
        iniFile.getValue(DefaultArchivectPrefs.SECTION_UI, formWindowGeometryKey(windowName), "")        
    }

    def setWindowGeometry(windowName: String, geometry: String): Unit = {
        iniFile.setValue(DefaultArchivectPrefs.SECTION_UI, formWindowGeometryKey(windowName), geometry)
    }

    private def formWindowGeometryKey(windowName: String): String = {
        DefaultArchivectPrefs.UI_GEOMETRY + "_" + windowName
    }
}