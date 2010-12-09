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
 * The UI application contexts.
 * 
 * @author matt
 *
 */
public final class ArchivectUIApplicationContexts {
    /**
     * No instances
     */
    private ArchivectUIApplicationContexts() {
        // nothing
    }

    /**
     * @return an array of standard application contexts used by
     * the UI.
     */
    public static String[] getApplicationContexts() {
        return new String[] {
                "org/devzendo/archivect/ArchivectUI.xml"
        };
    }

}
