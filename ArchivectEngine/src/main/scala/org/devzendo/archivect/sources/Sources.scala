/**
 * Copyright (C) 2008-2012 Matt Gumbley, DevZendo.org <http://devzendo.org>
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

package org.devzendo.archivect.sources

import scala.collection.mutable.{ ListBuffer}

import org.devzendo.archivect.sources.SourceFactory._
import collection.immutable.{TreeMap, TreeSet}

class Sources {
    var roots = new ListBuffer[String]()
    var unrootedSources = new TreeSet[UnrootedSource]
    var uncRootedSources = new TreeMap[String, Source] // keyed on root

    def getSources(): List[Source] = {
        val sources = new ListBuffer[Source]
        sources ++= unrootedSources
        sources += rootedSources
        sources.readOnly
    }


    def getRoots(): List[String] = {
        roots.readOnly
    }

}
