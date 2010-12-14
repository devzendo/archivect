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
 
package org.devzendo.archivect.gui

import org.devzendo.commonapp.gui.MainFrameFactory
import org.devzendo.commonapp.gui.WindowGeometryStore
import org.devzendo.commonapp.gui.CursorManager

/**
 * Constructs the ArchivectMainFrame and connects it with the rest of the app
 * framework.
 * 
 * @author matt
 * 
 */
class ArchivectMainFrameFactory(
    val cursorManager: CursorManager,
    val windowGeometryStore: WindowGeometryStore,
    val mainFrameFactory: MainFrameFactory)
{
    def createFrame: ArchivectMainFrame = {
        val mainFrame = new ArchivectMainFrame(windowGeometryStore)
        cursorManager.setMainFrame(mainFrame)
        mainFrameFactory.setMainFrame(mainFrame)
        return mainFrame;
    }
}