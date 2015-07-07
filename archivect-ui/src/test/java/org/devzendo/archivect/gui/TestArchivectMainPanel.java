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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIDefaults;
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

/**
 * Tests that the main panel can have its card layout manipulated.
 * 
 * @author matt
 *
 */
public final class TestArchivectMainPanel {
    private static final Logger LOGGER = Logger
            .getLogger(TestArchivectMainPanel.class);

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
                final UIDefaults uiDefaults = new UIDefaults();
                final Color color2 = uiDefaults.getColor(mainPanel);
                LOGGER.info("color2 is " + color2);
                LOGGER.info("main panel is " + mainPanel);
                
                return jFrame;
            }
        });
        window = new FrameFixture(frame);
        window.show();
        window.robot.waitForIdle();
    }
    
    @After
    public void tearDown() {
        LOGGER.info("Cleaning up window");
        window.cleanUp();
    }

    @Test(timeout = 3000)
    public void panelIsBlankOnStartup() {
        window.robot.waitForIdle();
        assertThat(getCurrentPanelName(), equalTo(ArchivectMainPanel.BlankPanelName()));
        //window.background().requireEqualTo(Color.white);
    }


    @Test(timeout = 3000)
    public void panelCanBeAddedTo() {
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
        assertThat(getCurrentPanelName(), equalTo("red"));
        // window.background().requireEqualTo(Color.RED);
    }

    @Test(timeout = 3000)
    public void panelAddDoesNotSwitchDisplay() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                final JPanel redPanel = createColouredPanel("red");
                mArchivectMainPanel.addPanel("red", redPanel);

                final JPanel bluePanel = createColouredPanel("blue");
                mArchivectMainPanel.addPanel("blue", bluePanel);

                mArchivectMainPanel.switchToPanel("red");
            }
        });
        window.robot.waitForIdle();
        assertThat(getCurrentPanelName(), equalTo("red"));
        // window.background().requireEqualTo(Color.RED);
    }

    @Test(timeout = 3000)
    public void removalOfNonCurrentPanelDoesNotChangeDisplay() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                final JPanel redPanel = createColouredPanel("red");
                mArchivectMainPanel.addPanel("red", redPanel);

                final JPanel bluePanel = createColouredPanel("blue");
                mArchivectMainPanel.addPanel("blue", bluePanel);

                mArchivectMainPanel.switchToPanel("red");
                
                mArchivectMainPanel.removePanel("blue");
            }
        });
        window.robot.waitForIdle();
        assertThat(getCurrentPanelName(), equalTo("red"));
        // window.background().requireEqualTo(Color.RED);
    }

    @Test(timeout = 3000)
    public void removalOfOnlyPanelChangesToBlankPanel() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                final JPanel redPanel = createColouredPanel("red");
                mArchivectMainPanel.addPanel("red", redPanel);

                mArchivectMainPanel.switchToPanel("red");

                mArchivectMainPanel.removePanel("red");
            }
        });
        window.robot.waitForIdle();
        assertThat(getCurrentPanelName(), equalTo(ArchivectMainPanel.BlankPanelName()));
    }

    @Test(timeout = 3000)
    public void removalOfCurrentPanelDoesChangeDisplayToPrevious() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                final JPanel redPanel = createColouredPanel("red");
                mArchivectMainPanel.addPanel("red", redPanel);

                final JPanel bluePanel = createColouredPanel("blue");
                mArchivectMainPanel.addPanel("blue", bluePanel);

                mArchivectMainPanel.switchToPanel("blue");

                mArchivectMainPanel.removePanel("blue");
            }
        });
        window.robot.waitForIdle();
        assertThat(getCurrentPanelName(), equalTo("red"));
        // window.background().requireEqualTo(Color.RED);
    }

    @Test(timeout = 3000)
    public void removalOfCurrentFirstPanelDoesChangeDisplayToNext() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                final JPanel redPanel = createColouredPanel("red");
                mArchivectMainPanel.addPanel("red", redPanel);

                final JPanel bluePanel = createColouredPanel("blue");
                mArchivectMainPanel.addPanel("blue", bluePanel);

                mArchivectMainPanel.switchToPanel("red");

                mArchivectMainPanel.removePanel("red");
            }
        });
        window.robot.waitForIdle();
        assertThat(getCurrentPanelName(), equalTo("blue"));
        // window.background().requireEqualTo(Color.RED);
    }

    private String getCurrentPanelName() {
        return GuiActionRunner.execute(new GuiQuery<String>() {
            @Override
            protected String executeInEDT() throws Throwable {
                return mArchivectMainPanel.currPanelName();
            }
        });
    }

    private JPanel createColouredPanel(final String colourName) {
        final JPanel panel = new JPanel();
        panel.setBackground(Color.getColor(colourName));
        return panel;
    }
}
