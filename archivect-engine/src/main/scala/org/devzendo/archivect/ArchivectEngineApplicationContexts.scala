/**
 * Copyright (C) 2008-2011 Matt Gumbley, DevZendo.org <http://devzendo.org>
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
 
package org.devzendo.archivect

object ArchivectEngineApplicationContexts {
    /**
     * @return an array of standard application contexts used by
     * the engine.
     */
    def getApplicationContexts(): Array[String] = {
        val contexts = new Array[String](2)
        contexts(0) = "org/devzendo/archivect/ArchivectEngine.xml"
        contexts(1) = "org/devzendo/archivect/ArchivectEnginePersistence.xml"
        contexts
    }
}