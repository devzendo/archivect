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

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.gui.DefaultCursorManager;
import org.devzendo.commonapp.gui.ThreadCheckingRepaintManager;
import org.devzendo.commonapp.gui.WindowGeometryStore;
import org.devzendo.commonapp.gui.WindowGeometryStorePersistence;
import org.devzendo.commonapp.gui.menu.MenuWiring;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * End-to-end test that when the main frame is closed, its WindowAdapter
 * triggers the File/Exit MenuIdentifier in MenuWiring, and that the
 * ActionListener that's bound to that is called, saving the geometry.
 * 
 * @author matt
 *
 */
@RunWith(JMock.class)
public class TestMainFrameCloseActionListener {
    private static final Logger LOGGER = Logger
            .getLogger(TestMainFrameCloseActionListener.class);
    private final Mockery context = new JUnit4Mockery();
    private FrameFixture window;
    private CursorManager mCursorManager;
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
        mCursorManager = new DefaultCursorManager();
        mWindowGeometryStorePersistence = context.mock(WindowGeometryStorePersistence.class);
        mWindowGeometryStore = new WindowGeometryStore(mWindowGeometryStorePersistence);
        mMenuWiring = new MenuWiring();
        
    }
    
    /**
     * 
     */
    @After
    public void tearDown() {
        LOGGER.info("Cleaning up window");
        window.cleanUp();
        mCursorManager.shutdown();
    }

    /**
     * 
     */
    @Test
    public void mainFrameResizeAndMoveStoresInPersistenceOnClose() {
        context.checking(new Expectations() { {
            allowing(mWindowGeometryStorePersistence).getWindowGeometry("main");
                will(returnValue("100,100,640,480"));
            atLeast(1).of(mWindowGeometryStorePersistence).setWindowGeometry("main", "200,200,500,400");
        } });
        final JFrame mainFrame = initialiseFrameFixture();

        mMenuWiring.setActionListener(
            ArchivectMenuIdentifiers.FILE_EXIT, 
            new MainFrameCloseActionListener(mWindowGeometryStore, mainFrame));
        
        window.robot.waitForIdle();

        mainFrame.setBounds(200, 200, 500, 400);
        window.robot.waitForIdle();
        mMenuWiring.triggerActionListener(ArchivectMenuIdentifiers.FILE_EXIT);
        window.robot.waitForIdle();
    }

    /**
     * Not sure about this test...
     */
    @Test
    public void mainFrameDisposesWhenFileExitMenuTriggered() {
        context.checking(new Expectations() { {
            allowing(mWindowGeometryStorePersistence).getWindowGeometry("main");
                will(returnValue("100,100,640,480"));
            atLeast(1).of(mWindowGeometryStorePersistence).setWindowGeometry(with(any(String.class)), with(any(String.class)));
        } });
        final JFrame mainFrame = initialiseFrameFixture();

        mMenuWiring.setActionListener(
            ArchivectMenuIdentifiers.FILE_EXIT, 
            new MainFrameCloseActionListener(mWindowGeometryStore, mainFrame));
        
        window.robot.waitForIdle();
        mMenuWiring.triggerActionListener(ArchivectMenuIdentifiers.FILE_EXIT);
        window.robot.waitForIdle();
        // not sure this is a precise way of determining whether the window
        // is disposed - there isn't an isDisposed() method.
        MatcherAssert.assertThat(mainFrame.isDisplayable(), Matchers.is(false));
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
