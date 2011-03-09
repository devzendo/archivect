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

/**
 * The main engine application contexts.
 * 
 * @author matt
 *
 */
public final class ArchivectEngineApplicationContexts {
    /**
     * No instances
     */
    private ArchivectEngineApplicationContexts() {
        // nothing
    }

    /**
     * @return an array of standard application contexts used by
     * the engine.
     */
    public static String[] getApplicationContexts() {
        return new String[] {
                "org/devzendo/archivect/ArchivectEngine.xml",
                "org/devzendo/archivect/ArchivectEnginePersistence.xml"
        };
    }

}
