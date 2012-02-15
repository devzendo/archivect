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

import scala.collection.mutable.ListBuffer

import org.devzendo.archivect.sources.SourceFactory._
import collection.immutable.TreeSet

/**
 * The SourcesRegistry allows Sources to be added,
 * and converted into SourceTrees. Sources of the same type,
 * under the same root are stored in the same SourceTree.
 *
 * The SourceTrees are returned in order.
 */
object SourcesRegistry {
    /**
     * Define an ordering of path components
     * a
     * a/b
     * a/c
     * z
     * z/x
     */
    def lessThan(s1: SourceFactory.Source, s2: Source): Boolean = {
        val pc1 = s1.pathComponents
        val pc2 = s2.pathComponents
        val maxlen = pc1.size.max(pc2.size)
//        println("pc1: " + pc1 + " pc2: " + pc2 + " maxlen: " + maxlen)
        for (ind <- 0 until maxlen) {
//            println("ind: " + ind)
            if (ind == pc1.size) {
//                println("pc1 out")
                return true
            }
            if (ind == pc2.size) {
//                println("pc2 out")
                return false
            }
            val c1 = pc1(ind)
            val c2 = pc2(ind)
//            println("c1: " + c1 + " c2: " + c2)
            if (c1 < c2) {
//                println("c1 lt")
                return true
            } else if (c1 > c2) {
//                println("c2 lt")
                return false
            }
        }
//        println("same")
        false
    }

    val pathOrdering = Ordering.fromLessThan[Source](lessThan)

}

class SourcesRegistry {

    var roots = new ListBuffer[String]()
    var unrootedSourceTrees = TreeSet.empty(SourcesRegistry.pathOrdering)
//    implicit def unrootedSource2
//    var uncRootedSources = new TreeMap[String, Source] // keyed on root

    def getSources(): List[Source] = {
        val sources = new ListBuffer[Source]
        //sources ++= unrootedSources
//        sources += rootedSources
        sources.readOnly
    }


    def getRoots(): List[String] = {
        roots.readOnly
    }

    def addSource(source: Source) = {
//        source match {
//            case unrootedSource: UnrootedSource =>
//          //      unrootedSources += unrootedSource
//            case rootedSource: RootedSource =>
//            case uncSource: UNCSource =>
//            case driveSource: WindowsDriveSource =>
//        }

    }
}
