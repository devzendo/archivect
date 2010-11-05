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
package org.devzendo.archivect;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.archivect.gui.ArchivectMainFrame;
import org.devzendo.commoncode.gui.GUIUtils;
import org.devzendo.commoncode.gui.ThreadCheckingRepaintManager;
import org.devzendo.commoncode.logging.Logging;
import org.devzendo.commonspring.springloader.SpringLoader;
import org.devzendo.minimiser.gui.Beautifier;

/**
 * The Archivect UI.
 * 
 * @author matt
 *
 */
public class ArchivectUIMain {
    private static final Logger LOGGER = Logger
            .getLogger(ArchivectUIMain.class);
    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        final List<String> finalArgList = Logging.getInstance().setupLoggingFromArgs(Arrays.asList(args));
        LOGGER.info("Starting Archivect UI");

        final String javaLibraryPath = System.getProperty("java.library.path");
        LOGGER.debug("java.library.path is '" + javaLibraryPath + "'");
        final File quaqua = new File(javaLibraryPath, "libquaqua.jnilib");
        LOGGER.debug("Quaqua JNI library exists there (for Mac OS X)? " + quaqua.exists());

        ThreadCheckingRepaintManager.initialise();

        final SpringLoader springLoader = new ArchivectSpringLoaderInitialiser().getSpringLoader();

        // Sun changed their recommendations and now recommends the UI be built
        // on the EDT, so I think flagging creation on non-EDT is OK.
        // "We used to say that you could create the GUI on the main thread as
        // long as you didn't modify components that had already been realized.
        // While this worked for most applications, in certain situations it
        // could cause problems."
        // http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
        // So let's create it on the EDT anyway
        //
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                try {
                    Beautifier.makeBeautiful();

                    // Process command line
                    for (int i = 0; i < finalArgList.size(); i++) {
                        final String arg = finalArgList.get(i);
                        LOGGER.debug("arg " + i + " = '" + arg + "'");
                    }
                    new ArchivectMainFrame(springLoader, finalArgList);
                } catch (final Exception e) {
                    LOGGER.fatal(e.getMessage());
                    System.exit(1);
                }
            }
        });
    }
}