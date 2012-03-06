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

        var rulePredicates = ListBuffer.empty[RulePredicate]
        var dirMap = Map.empty[String, DirNode]
        var sourcePathTermination = false

        //def sourcePathTermination = _sourcePathTermination

        def getDirNodes: Map[String, DirNode] = {
            dirMap
        }

        def addDir(name: String): DirNode = {
            val dn = dirMap.get(name)
            if (dn.isEmpty) {
                val newDirNode = new DirNode(name)
                println("adding node '" + name + "'")
                dirMap += (name -> newDirNode)
                newDirNode
            } else {
                dn.get
            }
        }

        def getRulePredicates: List[RulePredicate] = {
            rulePredicates.readOnly
        }

        def addRulePredicate(predicate: RulePredicate) {
            rulePredicates += predicate
        }
    }

    sealed abstract case class SourceTree(allPathsAreDirectoriesPredicate:
                                          SourcePredicate) {
        def addSource(source: Source) {
            var searchNode = rootNode
            println("adding source '" + source.path + "'")
            println("root node is: '" + searchNode.name + "' (" + searchNode.hashCode() + ")")
            source.pathComponents.foreach { (c: String) =>
                println("path component '" + c + "' search node was: '" + searchNode.name + "' (" +searchNode.hashCode() + ")")
                searchNode = searchNode.addDir(c)
                println("search node now: '" + searchNode.name + "' (" + searchNode.hashCode() + ")")

//                val subNodes = searchNode.getDirNodes
//                val subNode = subNodes.get(c)
//                if (subNode.isDefined) {
//                    println("node " + c + " is defined")
//                    searchNode = subNode.get
//                } else {
//                    println("node " + c + " is not defined; adding one")
//                    searchNode = searchNode.addDir(c)
//                }
            }
            println("adding source path termination to " + searchNode.hashCode())
            searchNode.sourcePathTermination = true
        }

        val rootNode = new DirNode("")
        def getRootNode: DirNode = {
            rootNode
        }
        def findNode(path: String): Option[DirNode] = {
            println("findNode searching for path '" + path + "'")
            val source = SourceFactory.pathToSource(path)
            println("searching for path as source '" + source.pathComponents + "'")
            var searchNode = rootNode
            source.pathComponents.foreach { (c: String) =>
                println("path component '" + c + "'...")
                println("search node now: '" + searchNode.name + "' (" + searchNode.hashCode() + ")")

                val subNodes = searchNode.getDirNodes
                subNodes.keys.foreach((c:String) => println("dir name key: " + c))
                val subNode = subNodes.get(c)
                if (subNode.isDefined) {
                    println("found node " + c + " in dir '" + searchNode.name + "'")
                    searchNode = subNode.get
                } else {
                    println("node " + c + " not found in dir '" + searchNode.name + "'")
                    return None
                }
            }
            println("yes, got that node")
            return Some(searchNode)
        }

        def getRulesAtDir(path: String): List[RulePredicate] = {
            val source = SourceFactory.pathToSource(path)
            // TODO
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
            
            // TODO rules must be in the subtree of sources
            def throwSinceRuleIsNotAtOrUnderSourceTree() {
                throw new SourceTreeException("Cannot add rule '" +
                    predicate.rule.ruleText + "' at '" +
                    predicate.rule.ruleAt + "': rules can only be added at, or under source paths")
            }
            var rulePlacementNode = rootNode
            var seenSourceTermination = false
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
            rulePlacementNode.addRulePredicate(predicate)


//            var node = rootNode
//            ruleAtSource.pathComponents.foreach((c: String) => node = node.addDir(c))
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
