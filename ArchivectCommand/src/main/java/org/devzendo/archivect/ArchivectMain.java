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

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.logging.Logging;
import org.devzendo.commonspring.springloader.SpringLoader;

/**
 * The Archivect main command line program.
 * 
 * @author matt
 *
 */
public class ArchivectMain {
    private static final Logger LOGGER = Logger.getLogger(ArchivectMain.class);
    
    /**
     * @param args the command line arguments.
     */
    public static void main(final String[] args) {
        final List<String> finalArgList = Logging.getInstance().setupLoggingFromArgs(Arrays.asList(args));

        final SpringLoader springLoader = new ArchivectSpringLoaderInitialiser().getSpringLoader();

        LOGGER.info("Hello world from Archivect");
    }

}