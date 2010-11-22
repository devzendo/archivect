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

import org.apache.log4j.Logger;
import org.devzendo.commonapp.spring.springloader.SpringLoader;
import org.devzendo.commonapp.spring.springloader.SpringLoaderFactory;
import org.devzendo.commoncode.string.StringUtils;

/**
 * Initialise the Archivect Application Contexts via the SpringLoader.
 * @author matt
 *
 */
public final class ArchivectSpringLoaderInitialiser {
    private static final Logger LOGGER = Logger
            .getLogger(ArchivectSpringLoaderInitialiser.class);
    private final SpringLoader mSpringLoader;

    /**
     * Initialise the SpringLoader with the Archivect Application Contexts
     */
    public ArchivectSpringLoaderInitialiser() {
        // Now load up Spring...
        final long startSpring = System.currentTimeMillis();
        mSpringLoader = SpringLoaderFactory
                .initialise(ArchivectEngineApplicationContexts.getApplicationContexts());
        final long stopSpring = System.currentTimeMillis();
        final long springElapsed = stopSpring - startSpring;
        LOGGER.debug("SpringLoader initialised in "
                + StringUtils.translateTimeDuration(springElapsed));
    }

    /**
     * @return the Archivect SpringLoader
     */
    public SpringLoader getSpringLoader() {
        return mSpringLoader;
    }
}
