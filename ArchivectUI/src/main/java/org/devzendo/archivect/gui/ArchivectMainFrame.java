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
package org.devzendo.archivect.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.devzendo.commonapp.gui.MainFrameFactory;
import org.devzendo.commonapp.gui.WindowGeometryStore;
import org.devzendo.commoncode.resource.ResourceLoader;


/**
 * The Archivect UI Main Frame, saves its geometry, and handles closing.
 * @author matt
 *
 */
public class ArchivectMainFrame extends JFrame {
    private static final String MAIN_FRAME_NAME = "main";
    private final WindowGeometryStore mWindowGeometryStore;

    public ArchivectMainFrame(final WindowGeometryStore windowGeometryStore, final MainFrameFactory mainFrameFactory) {
        mWindowGeometryStore = windowGeometryStore;
        
        setIconImage(ResourceLoader.createResourceImageIcon("org/devzendo/archivect/icons/application.gif").getImage());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setMainFrameInFactory();
        
        setName(MAIN_FRAME_NAME);
        setLayout(new BorderLayout());

        // TODO inject me....
        final JPanel mainPanel = new JPanel(); // TODO to be replaced by the main app panel
        mainPanel.setPreferredSize(new Dimension(640, 480));
        add(mainPanel, BorderLayout.CENTER);
        
        loadInitialGeometry();
    }

    private void setMainFrameInFactory() {
        // TODO Auto-generated method stub
        
    }

    private void loadInitialGeometry() {
        if (!mWindowGeometryStore.hasStoredGeometry(this)) {
            pack();
        }
        mWindowGeometryStore.loadGeometry(this);
    }
}
