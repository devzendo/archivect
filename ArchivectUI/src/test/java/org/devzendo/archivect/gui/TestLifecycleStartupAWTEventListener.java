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

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.gui.ThreadCheckingRepaintManager;
import org.devzendo.commonapp.lifecycle.Lifecycle;
import org.devzendo.commonapp.lifecycle.LifecycleManager;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
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
 * Tests for correct interaction of the CursorManager and LifecycleManager
 * during main window creation.
 *  
 * @author matt
 *
 */
@RunWith(JMock.class)
public final class TestLifecycleStartupAWTEventListener {
    private static final Logger LOGGER = Logger
            .getLogger(TestMainFrameCloseActionListener.class);
    // TODO: make this threadsafe when jmock 2.6.0 comes out
    // see http://www.natpryce.com/articles/000762.html
    // and uncomment the expectations indicated below to replicate a fail
    // which goes unreported.
    private final Mockery context = new JUnit4Mockery();
    private FrameFixture window;
    private CursorManager mCursorManager;
    private LifecycleManager mLifecycleManager;
    private LifecycleStartupAWTEventListener mListener;

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
        mCursorManager = context.mock(CursorManager.class);
        mLifecycleManager = context.mock(LifecycleManager.class);
    }
    
    /**
     * 
     */
    @After
    public void tearDown() {
        LOGGER.info("Cleaning up window");
    
        if (mListener != null) {
            GuiActionRunner.execute(new GuiTask() {
                @Override
                protected void executeInEDT() throws Throwable {
                    Toolkit.getDefaultToolkit().removeAWTEventListener(mListener);
                }
            });
        }

        window.cleanUp();
    }
    
    /**
     * 
     */
    @Test(timeout = 2000)
    public void cursorManagerShowsHourglass() {
        context.checking(new Expectations() { {
            // then
            oneOf(mCursorManager).hourglass(with(Expectations.any(String.class)));
            // also
            allowing(mLifecycleManager).startup();
            // TODO: comment this out to have the test fail, but not report it
            // to junit. Thread safe jmock should fix this?
            oneOf(mCursorManager).normal(with(Expectations.any(String.class)));
        } });
        // given
        final JFrame mainFrame = initialiseFrameFixture();
        mListener = new LifecycleStartupAWTEventListener(mainFrame, mCursorManager, mLifecycleManager);
        attachListener();
        window.robot.waitForIdle();
        // when
        window.show(); // shows the frame to test
        window.robot.waitForIdle();
    }

    /**
     * @throws InterruptedException never
     */
    @Test(timeout = 2000)
    public void lifecycleStartupHappensInOwnThread() throws InterruptedException {
        context.checking(new Expectations() { {
            // also
            oneOf(mCursorManager).hourglass(with(Expectations.any(String.class)));
            // TODO: comment this out to have the test fail, but not report it
            // to junit. Thread safe jmock should fix this?
            oneOf(mCursorManager).normal(with(Expectations.any(String.class)));
        } });
        // given
        final JFrame mainFrame = initialiseFrameFixture();
        final CountDownLatch latch = new CountDownLatch(1);
        final StubLifecycleManager lifecycleManager = new StubLifecycleManager(latch);
        MatcherAssert.assertThat(lifecycleManager.getStartupThread(), Matchers.nullValue());
        mListener = new LifecycleStartupAWTEventListener(mainFrame, mCursorManager, lifecycleManager);
        attachListener();
        window.robot.waitForIdle();
        // when
        window.show(); // shows the frame to test
        window.robot.waitForIdle();
        // then
        latch.await();
        final Thread startupThread = lifecycleManager.getStartupThread();
        MatcherAssert.assertThat(startupThread, Matchers.not(Matchers.sameInstance(Thread.currentThread())));
        MatcherAssert.assertThat(startupThread.getName(), Matchers.equalTo("Lifecycle Startup"));
    }

    /**
     * @throws InterruptedException never
     */
    @Test(timeout = 2000)
    public void cursorManagerHourglassEnds() throws InterruptedException {
        context.checking(new Expectations() { {
            // then
            oneOf(mCursorManager).normal(with(Expectations.any(String.class)));
            // also
            allowing(mCursorManager).hourglass(with(Expectations.any(String.class)));
            allowing(mLifecycleManager).startup();
        } });
        // given
        final JFrame mainFrame = initialiseFrameFixture();
        mListener = new LifecycleStartupAWTEventListener(mainFrame, mCursorManager, mLifecycleManager);
        attachListener();
        window.robot.waitForIdle();
        // when
        window.show(); // shows the frame to test
        window.robot.waitForIdle();
    }

    private class StubLifecycleManager implements LifecycleManager {
        private Thread mStartupThread;
        private final CountDownLatch mLatch;
        public StubLifecycleManager(final CountDownLatch latch) {
            mLatch = latch;
        }
        
        public final Thread getStartupThread() {
            return mStartupThread;
        }

        public void shutdown() {
            // do nothing
        }

        public void startup() {
            mStartupThread = Thread.currentThread();
            mLatch.countDown();
        }

        public Lifecycle getBean(final String beanName) {
            return null;
        }

        public List<String> getBeanNames() {
            return null;
        }

        public List<Lifecycle> getBeans() {
            return null;
        }
    }

    private void attachListener() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                Toolkit.getDefaultToolkit().addAWTEventListener(mListener, AWTEvent.WINDOW_EVENT_MASK);
            }
        });
    }

    private JFrame initialiseFrameFixture() {
        final JFrame frame = GuiActionRunner.execute(new GuiQuery<JFrame>() {
            @Override
            protected JFrame executeInEDT() {
                return new JFrame();
            }
        });
        window = new FrameFixture(frame);
        return frame;
    }
}
