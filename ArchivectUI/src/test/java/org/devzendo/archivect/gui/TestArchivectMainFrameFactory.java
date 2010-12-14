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
import org.devzendo.commonapp.gui.ThreadCheckingRepaintManager;
import org.devzendo.commonapp.gui.WindowGeometryStore;
import org.devzendo.commonapp.gui.WindowGeometryStorePersistence;
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
 * The ArchivectUI main frame factory sets up the main frame.
 * 
 * @author matt
 * 
 */
@RunWith(JMock.class)
public final class TestArchivectMainFrameFactory {
    private final Mockery context = new JUnit4Mockery();
    private FrameFixture window;
    private CursorManager mCursorManager;
    private WindowGeometryStorePersistence mWindowGeometryStorePersistence;
    private WindowGeometryStore mWindowGeometryStore;
    private MainFrameFactory mMainFrameFactory;

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
        mCursorManager = new CursorManager();
        mMainFrameFactory = new MainFrameFactory();
        mWindowGeometryStorePersistence = context.mock(WindowGeometryStorePersistence.class);
        mWindowGeometryStore = new WindowGeometryStore(mWindowGeometryStorePersistence);
    }
    
    /**
     * 
     */
    @After
    public void tearDown() {
        window.cleanUp();
        mCursorManager.shutdown();
    }

    /**
     * @throws Exception never
     */
    @Test
    public void mainFrameIsCorrectlySetUp() throws Exception {
        context.checking(new Expectations() { {
            allowing(mWindowGeometryStorePersistence).getWindowGeometry("main");
                will(returnValue("100,100,640,480"));
            ignoring(mWindowGeometryStorePersistence);
        } });

        final JFrame mainFrame = initialiseFrameFixture();
        
        MatcherAssert.assertThat(mainFrame, Matchers.notNullValue());
        MatcherAssert.assertThat(mCursorManager.getMainFrame(), Matchers.equalTo(mainFrame));
        MatcherAssert.assertThat((JFrame) mMainFrameFactory.getObject(), Matchers.equalTo(mainFrame));
        mainFrame.dispose();
    }
    
    /**
     * 
     */
    @Test
    public void mainFrameResizeAndMoveStoresInPersistence() {
        context.checking(new Expectations() { {
            allowing(mWindowGeometryStorePersistence).getWindowGeometry("main");
                will(returnValue("100,100,640,480"));
            atLeast(1).of(mWindowGeometryStorePersistence).setWindowGeometry("main", "200,200,500,400");
        } });
        final JFrame mainFrame = initialiseFrameFixture();
        window.robot.waitForIdle();

        mainFrame.setBounds(200, 200, 500, 400);
        window.robot.waitForIdle();
        mainFrame.dispose();
        window.robot.waitForIdle();
    }

    private JFrame initialiseFrameFixture() {
        final ArchivectMainFrame frame = GuiActionRunner.execute(new GuiQuery<ArchivectMainFrame>() {
            @Override
            protected ArchivectMainFrame executeInEDT() {
                return new ArchivectMainFrameFactory(mCursorManager, mWindowGeometryStore, mMainFrameFactory).createFrame();
            }
        });
        window = new FrameFixture(frame);
        window.show(); // shows the frame to test
        return frame;
    }
}
