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

import scala.collection.mutable.Map
import org.devzendo.archivect.rule.RulePredicate
import org.devzendo.archivect.sources.SourceFactory._
import scala.throws

object SourceTreeFactory {
    case class DirNode(name: String) {

        val dirMap = Map.empty[String, DirNode]

        def getDirNodes: Map[String, DirNode] = {
            dirMap
        }

        def addDir(name: String): DirNode = {
            val dn = dirMap.get(name)
            if (dn.isEmpty) {
                val newDirNode = new DirNode(name)
                dirMap += (name -> newDirNode)
                newDirNode
            } else {
                dn.get
            }
        }
    }

    sealed abstract case class SourceTree(allPathsAreDirectoriesPredicate:
                                          SourcePredicate) {
        val rootNode = new DirNode("")
        def getRootNode: DirNode = {
            rootNode
        }

        def getRulesAtDir(path: String): List[RulePredicate] = {
            val source = SourceFactory.pathToSource(path)
            List.empty[RulePredicate]
        }

        @throws(classOf[SourceTreeException])
        def addIncludeRule(predicate: RulePredicate) {
            val ruleAtSource = SourceFactory.pathToSource(predicate.rule.ruleAt)

            if (!allPathsAreDirectoriesPredicate(ruleAtSource)) {
                throw new SourceTreeException("Cannot add rule '" +
                    predicate.rule.ruleText + "' at '" +
                    predicate.rule.ruleAt + "': rules can only be added to directories")
            }
        }

    }
    case class UnrootedSourceTree(
         override val allPathsAreDirectoriesPredicate: SourcePredicate)
        extends SourceTree(allPathsAreDirectoriesPredicate) {
    }

    case class RootedSourceTree(
         override val allPathsAreDirectoriesPredicate: SourcePredicate, root: String)
        extends SourceTree(allPathsAreDirectoriesPredicate)

    case class WindowsDriveSourceTree(
         override val allPathsAreDirectoriesPredicate: SourcePredicate,
         driveLetter: String)
        extends RootedSourceTree(allPathsAreDirectoriesPredicate, driveLetter)

    // Not sure I want to support UNC paths as sources
    case class UNCSourceTree(
         override val allPathsAreDirectoriesPredicate: SourcePredicate, server: String,
         share: String)
        extends RootedSourceTree(allPathsAreDirectoriesPredicate, """\\""" + server + """\""" + share)
}
