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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.WindowGeometryStore;
import org.devzendo.commoncode.resource.ResourceLoader;


/**
 * The Archivect UI Main Frame, saves its geometry, and handles closing.
 * @author matt
 *
 */
public class ArchivectMainFrame extends JFrame {
    private static final Logger LOGGER = Logger
            .getLogger(ArchivectMainFrame.class);
    private static final String MAIN_FRAME_NAME = "main";
    private final WindowGeometryStore mWindowGeometryStore;

    public ArchivectMainFrame(final WindowGeometryStore windowGeometryStore) {
        mWindowGeometryStore = windowGeometryStore;
        
        setIconImage(ResourceLoader.createResourceImageIcon("org/devzendo/archivect/icons/application.gif").getImage());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        setName(MAIN_FRAME_NAME);
        setLayout(new BorderLayout());

        // TODO inject me....
        final JPanel mainPanel = new JPanel(); // TODO to be replaced by the main app panel
        mainPanel.setPreferredSize(new Dimension(640, 480));
        add(mainPanel, BorderLayout.CENTER);
        
        loadInitialGeometry();
        setupGeometrySaveOnMoveOnClose();
    }

    private void loadInitialGeometry() {
        if (!mWindowGeometryStore.hasStoredGeometry(this)) {
            pack();
        }
        mWindowGeometryStore.loadGeometry(this);
    }

    private void setupGeometrySaveOnMoveOnClose() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                LOGGER.info("Saving geometry");
                mWindowGeometryStore.saveGeometry(ArchivectMainFrame.this);
                LOGGER.info("Closing window");
                dispose();
                LOGGER.info("Exitting");
                System.exit(0);
            }
        });
    }
}

