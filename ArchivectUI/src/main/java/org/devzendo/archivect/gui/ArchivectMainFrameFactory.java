/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.devzendo.archivect.gui;

import javax.swing.JFrame;

import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.gui.MainFrameFactory;
import org.devzendo.commonapp.gui.WindowGeometryStore;

/**
 * Constructs the ArchivectMainFrame and connects it with the rest of the app
 * framework.
 * 
 * @author matt
 * 
 */
public final class ArchivectMainFrameFactory {
    private final CursorManager mCursorManager;

    private final WindowGeometryStore mWindowGeometryStore;

    private final MainFrameFactory mMainFrameFactory;

    /**
     * Construct the factory with the objects it needs from the rest of the
     * framework.
     * 
     * @param cursorManager
     *        the CursorManager
     * @param windowGeometryStore
     *        the WindowGeometryStore
     * @param mainFrameFactory
     *        the Spring MainFrameFactory bean.
     */
    public ArchivectMainFrameFactory(final CursorManager cursorManager,
            final WindowGeometryStore windowGeometryStore,
            final MainFrameFactory mainFrameFactory) {
        mCursorManager = cursorManager;
        mWindowGeometryStore = windowGeometryStore;
        mMainFrameFactory = mainFrameFactory;
    }

    /**
     * @return the main application frame, connected to the app framework.
     */
    public JFrame createFrame() {
        final ArchivectMainFrame archivectMainFrame = new ArchivectMainFrame(
                mWindowGeometryStore);
        mCursorManager.setMainFrame(archivectMainFrame);
        mMainFrameFactory.setMainFrame(archivectMainFrame);
        return archivectMainFrame;
    }
}
