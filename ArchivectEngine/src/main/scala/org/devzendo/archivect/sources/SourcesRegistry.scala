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
import collection.immutable.TreeMap

import org.devzendo.archivect.sources.SourceFactory._
import org.devzendo.archivect.sources.SourceTreeFactory._

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
class SourcesRegistry(
    val allPathsAreDirectoriesPredicate: SourcePredicate) {

    var roots = new ListBuffer[String]()
    var unrootedSourceTree: Option[UnrootedSourceTree] = None
    var rootedSourceTrees = new TreeMap[String, SourceTree] // keyed on root
    var windowsDriveSourceTrees = new TreeMap[String, WindowsDriveSourceTree] // keyed on root
    var uncSourceTrees = new TreeMap[String, UNCSourceTree] // keyed on root

    def getSourceTrees: List[SourceTree] = {
        val sourceTrees = new ListBuffer[SourceTree]
        if (unrootedSourceTree.isDefined) {
            sourceTrees += unrootedSourceTree.get
        }
        sourceTrees ++= rootedSourceTrees.values
        sourceTrees ++= windowsDriveSourceTrees.values
        sourceTrees ++= uncSourceTrees.values
        sourceTrees.readOnly
    }

    def getRoots: List[String] = {
        roots.readOnly
    }

    def addSource(source: Source): SourceTree = {
        val sourceTree = getSourceTree(source)
        sourceTree.addSource(source)
        return sourceTree
    }

    private[this] def getSourceTree(source: Source): SourceTree = {
        source match {
            case unrootedSource: UnrootedSource =>
                if (unrootedSourceTree.isEmpty) {
                   unrootedSourceTree = Some(UnrootedSourceTree
                    (allPathsAreDirectoriesPredicate))
                }
                unrootedSourceTree.get
            case uncSource: UNCSource =>
                val root = uncSource.root
                val tree = uncSourceTrees.get(root)
                if (tree.isEmpty) {
                    val newRoot = new UNCSourceTree(allPathsAreDirectoriesPredicate, uncSource.server, uncSource.share)
                    uncSourceTrees = uncSourceTrees.insert(root, newRoot)
                    newRoot
                } else {
                    tree.get
                }
            case driveSource: WindowsDriveSource =>
                val root = driveSource.root
                val tree = windowsDriveSourceTrees.get(root)
                if (tree.isEmpty) {
                    val newRoot = WindowsDriveSourceTree(allPathsAreDirectoriesPredicate, driveSource.driveLetter)
                    windowsDriveSourceTrees = windowsDriveSourceTrees.insert(root, newRoot)
                    newRoot
                } else {
                    tree.get
                }
            case rootedSource: RootedSource =>
                val root = rootedSource.root
                val tree = rootedSourceTrees.get(root)
                if (tree.isEmpty) {
                    val newRoot = RootedSourceTree(allPathsAreDirectoriesPredicate, root)
                    rootedSourceTrees = rootedSourceTrees.insert(root, newRoot)
                    newRoot
                } else {
                    tree.get
                }
        }
    }
}
