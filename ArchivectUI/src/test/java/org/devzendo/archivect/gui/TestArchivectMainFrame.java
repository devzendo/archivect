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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.ThreadCheckingRepaintManager;
import org.devzendo.commonapp.gui.WindowGeometryStore;
import org.devzendo.commonapp.gui.WindowGeometryStorePersistence;
import org.devzendo.commonapp.gui.menu.MenuWiring;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The ArchivectUI main frame triggers the FileExit action listener via the
 * MenuWiring on close.
 * 
 * @author matt
 * 
 */
@RunWith(JMock.class)
public final class TestArchivectMainFrame {
    private static final Logger LOGGER = Logger
            .getLogger(TestArchivectMainFrame.class);
    private final Mockery context = new JUnit4Mockery();
    private FrameFixture window;
    private WindowGeometryStorePersistence mWindowGeometryStorePersistence;
    private WindowGeometryStore mWindowGeometryStore;
    private MenuWiring mMenuWiring;

    /**
     * 
     */
    @BeforeClass
    public static void setUpLogging() {
        LoggingUnittestHelper.setupLogging();
        ThreadCheckingRepaintManager.initialise();
    }

    /**
     * 
     */
    @Before
    public void setUp() {
        mWindowGeometryStorePersistence = context.mock(WindowGeometryStorePersistence.class);
        mWindowGeometryStore = new WindowGeometryStore(mWindowGeometryStorePersistence);
        mMenuWiring = new MenuWiring();
    }
    
    /**
     * 
     */
    @After
    public void tearDown() {
        window.cleanUp();
    }

    /**
     * @throws Exception never
     */
    @Test
    public void mainFrameTriggersFileExitActionListenerViaMenuWiringOnDispose() throws Exception {
        final boolean[] seenActionListener = {false};
        // Could store a menu item for the menu identifier here, but it's not
        // necessary any more.
        mMenuWiring.setActionListener(ArchivectMenuIdentifiers.FILE_EXIT, new ActionListener() {
            public void actionPerformed(final ActionEvent arg0) {
                LOGGER.debug("ActionListener has been triggered");
                seenActionListener[0] = true;
            } });
        context.checking(new Expectations() { {
            allowing(mWindowGeometryStorePersistence).getWindowGeometry("main");
                will(returnValue("100,100,640,480"));
            ignoring(mWindowGeometryStorePersistence);
        } });

        final JFrame mainFrame = initialiseFrameFixture();
        window.robot.waitForIdle();
        Assert.assertFalse(seenActionListener[0]);
        
        mainFrame.dispose();
        window.robot.waitForIdle();
        Assert.assertTrue(seenActionListener[0]);
    }
   
    private JFrame initialiseFrameFixture() {
        final ArchivectMainFrame frame = GuiActionRunner.execute(new GuiQuery<ArchivectMainFrame>() {
            @Override
            protected ArchivectMainFrame executeInEDT() {
                return new ArchivectMainFrame(mWindowGeometryStore, mMenuWiring);
            }
        });
        window = new FrameFixture(frame);
        window.show(); // shows the frame to test
        return frame;
    }
}
