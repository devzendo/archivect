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

import org.devzendo.commonapp.prefs.Prefs

/**
 * Storage API for user preferences. Encapsulates the storage mechanism.
 * 
 * @author matt
 *
 */
trait ArchivectPrefs extends Prefs {

    /**
     * The various sections that the Prefs storage is split into. Changes of
     * preference settings in any of these sections can be listened for.
     * 
     * Although all sections are listed here, not all sections will have
     * changes propagated; consult the JavaDoc for each section's mutator
     * methods to see if a change would be propagated. 
     *
     */
    object PrefsSection extends Enumeration {
        val Geometry = Value
    }

    /**
     * Obtain the stored Window Geometry
     * @param windowName a window name
     * @return a String of the form x,y,width,height.
     */
    def getWindowGeometry(windowName: String): String

    /**
     * Store the Window Geometry
     * @param windowName a window name
     * @param geometry a String of the form x,y,width,height.
     */
    def setWindowGeometry(windowName: String, geometry: String): Unit


}