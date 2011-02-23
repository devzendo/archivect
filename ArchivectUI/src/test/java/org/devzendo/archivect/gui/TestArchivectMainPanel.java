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

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.ThreadCheckingRepaintManager;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

//@RunWith(JMock.class)
public class TestArchivectMainPanel {
    private static final Logger LOGGER = Logger
            .getLogger(TestArchivectMainPanel.class);
    /**
     * 
     */
    @BeforeClass
    public static void setUpLogging() {
        LoggingUnittestHelper.setupLogging();
        ThreadCheckingRepaintManager.initialise();
    }
    private FrameFixture window;
    private ArchivectMainPanel mArchivectMainPanel;

    @Before
    public void createFrame() {
        mArchivectMainPanel = GuiActionRunner.execute(new GuiQuery<ArchivectMainPanel>() {
            @Override
            protected ArchivectMainPanel executeInEDT() {
                final ArchivectMainPanel archivectMainPanel = new ArchivectMainPanel();
                return archivectMainPanel;
            }
        });
        final JFrame frame = GuiActionRunner.execute(new GuiQuery<JFrame>() {
            @Override
            protected JFrame executeInEDT() {
                final JFrame jFrame = new JFrame();
                final JPanel mainPanel = mArchivectMainPanel.mainPanel();
                final Color color = UIManager.getDefaults().getColor(mainPanel);
                LOGGER.info(color);
                jFrame.setContentPane(mainPanel);
                return jFrame;
            }
        });
        window = new FrameFixture(frame);
        window.show();
        window.robot.waitForIdle();
    }
    
    /**
     * 
     */
    @After
    public void tearDown() {
        LOGGER.info("Cleaning up window");
        window.cleanUp();
    }

    @Test(timeout = 3000)
    public void panelIsBlankOnStartup()
    {
        window.background().requireEqualTo(Color.white);
    }

    @Test(timeout = 3000)
    public void panelCanBeAddedTo()
    {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                final JPanel redPanel = new JPanel();
                redPanel.setBackground(Color.RED);
                mArchivectMainPanel.addPanel("red", redPanel);
                mArchivectMainPanel.switchToPanel("red");
            }
        });
        window.robot.waitForIdle();
        window.background().requireEqualTo(Color.RED);
    }
}
