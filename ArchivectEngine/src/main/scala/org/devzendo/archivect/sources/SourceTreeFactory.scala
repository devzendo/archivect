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

import org.devzendo.archivect.rule.RulePredicate
import org.devzendo.archivect.sources.SourceFactory._
import scala.throws
import collection.mutable.{ListBuffer, Map}

object SourceTreeFactory {
    case class DirNode(name: String) {

        var rulePredicates = ListBuffer.empty[(Boolean, RulePredicate)]
        var dirMap = Map.empty[String, DirNode]
        var sourcePathTermination = false

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

        def getRulePredicates: List[(Boolean, RulePredicate)] = {
            rulePredicates.readOnly
        }

        def addRulePredicate(inclusion: Boolean, predicate: RulePredicate) {
            val tuple = (inclusion, predicate)
            rulePredicates += tuple
        }
    }

    sealed abstract case class SourceTree(allPathsAreDirectoriesPredicate:
                                          SourcePredicate) {
        val rootNode = new DirNode("")

        def addSource(source: Source) {
            var searchNode = rootNode
            source.pathComponents.foreach { (c: String) =>
                searchNode = searchNode.addDir(c)
            }
            searchNode.sourcePathTermination = true
        }

        def _getRootNode: DirNode = {
            rootNode
        }

        def _findNode(path: String): Option[DirNode] = {
            val source = SourceFactory.pathToSource(path)
            var searchNode = rootNode
            source.pathComponents.foreach { (c: String) =>
                val subNodes = searchNode.getDirNodes
                val subNode = subNodes.get(c)
                if (subNode.isDefined) {
                    searchNode = subNode.get
                } else {
                    return None
                }
            }
            Some(searchNode)
        }

        def getRulesAtDir(path: String): List[(Boolean, RulePredicate)] = {
            val list = for {
                pathNode <- _findNode(path)
            } yield pathNode.getRulePredicates
            list.getOrElse(List.empty[(Boolean, RulePredicate)])
        }

        def _addRule(inclusion: Boolean, predicate: RulePredicate) {
            val ruleAtSource = SourceFactory.pathToSource(predicate.rule.ruleAt)

            if (!allPathsAreDirectoriesPredicate(ruleAtSource)) {
                throw new SourceTreeException("Cannot add rule '" +
                    predicate.rule.ruleText + "' at '" +
                    predicate.rule.ruleAt + "': rules can only be added to directories")
            }

            def throwSinceRuleIsNotAtOrUnderSourceTree() {
                throw new SourceTreeException("Cannot add rule '" +
                    predicate.rule.ruleText + "' at '" +
                    predicate.rule.ruleAt + "': rules can only be added at, or under source paths")
            }

            var rulePlacementNode = rootNode
            var seenSourceTermination = rulePlacementNode.sourcePathTermination

            ruleAtSource.pathComponents.foreach { (c: String) =>
                val subNodes = rulePlacementNode.getDirNodes
                val subNode = subNodes.get(c)
                if (seenSourceTermination) {
                    if (subNode.isEmpty) {
                        rulePlacementNode = rulePlacementNode.addDir(c)
                    } else {
                        rulePlacementNode = subNode.get
                    }
                } else {
                    if (subNode.isEmpty) {
                        throwSinceRuleIsNotAtOrUnderSourceTree()
                    }
                    seenSourceTermination |= subNode.get.sourcePathTermination
                    rulePlacementNode = subNode.get
                }
            }
            if (!seenSourceTermination) {
                throwSinceRuleIsNotAtOrUnderSourceTree()
            }
            rulePlacementNode.addRulePredicate(inclusion, predicate)
        }

        @throws(classOf[SourceTreeException])
        def addIncludeRule(predicate: RulePredicate) {
            _addRule(true, predicate);
        }

        @throws(classOf[SourceTreeException])
        def addExcludeRule(predicate: RulePredicate) {
            _addRule(false, predicate);
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
