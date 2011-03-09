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

package org.devzendo.archivect.destinations

import org.apache.log4j.Logger
import collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.xml.{Document, XML, Elem, Node, NodeSeq}
import java.io.{File, FileWriter}

object DefaultDestinations {
    private val LOGGER = Logger.getLogger(classOf[DefaultDestinations])
}

class DefaultDestinations(val destinationsPath: String) extends Destinations {
    DefaultDestinations.LOGGER.debug("Destinations path is '" + destinationsPath + "'")
    private val destinationsFile = new File(destinationsPath)
    private val destinations = ListBuffer.empty[Destination] 
    
    createDestinationsIfStorageDoesNotExist()
    loadDestinations()
    
    def size() = destinations.size
    
    def getDestination(index: Int) = {
        destinations(index)
    }
    
    def addDestination(dest: Destination) = {
        destinations += dest
        saveDestinations
    }

    def summaries: List[DestinationSummary] = {
        (destinations map (summarise(_))).toList
    }
    
    private def summarise(destination: Destination): DestinationSummary = {
        destination match {
            case LocalDestination(name, localPath) => 
                new DestinationSummary(name, "local")
            case SmbDestination(name, server, share, userName, password, localPath) =>
                new DestinationSummary(name, "smb")
        }
    }

    private def createDestinationsIfStorageDoesNotExist() = {
        if (!destinationsFile.exists()) {
            DefaultDestinations.LOGGER.debug("Creating initial destinations file")
            val doc = <destinations/>
            xml.XML.save(destinationsPath, doc, "UTF-8", true)
            DefaultDestinations.LOGGER.debug("Created")
        }
    }
    
    private def loadDestinations() = {
        destinations.clear()
        val destinationsNode = XML.loadFile(destinationsPath)
        val localDestinations = destinationsNode \ "localDestination"
        destinations ++= localDestinations map (deserialiseLocal(_))
        val smbDestinations = destinationsNode \ "smbDestination"
        destinations ++= smbDestinations map (deserialiseSmb(_))
    }
    
    private def deserialiseLocal(node: Node): LocalDestination = 
        new LocalDestination((node \ "@name").toString,
                             (node \ "@localPath").toString)

    private def deserialiseSmb(node: Node): SmbDestination = 
        new SmbDestination((node \ "@name").toString,
                           (node \ "@server").toString,
                           (node \ "@share").toString,
                           (node \ "@userName").toString,
                           (node \ "@password").toString,
                           (node \ "@localPath").toString)

    private def saveDestinations() = {
        DefaultDestinations.LOGGER.debug("Saving destinations file")
        val doc =
            <destinations>
            {
                destinations map (serialise(_))
            }
            </destinations>
        xml.XML.save(destinationsPath, doc, "UTF-8", true)
        DefaultDestinations.LOGGER.debug("Saved")
    }
    
    private def serialise(destination: Destination): Elem = {
        destination match {
            case LocalDestination(name, localPath) => 
                <localDestination name={name} localPath={localPath}/>
            case SmbDestination(name, server, share, userName, password, localPath) =>
                <smbDestination name={name} server={server} share={share} userName={userName} password={password} localPath={localPath} />
        }
    }
}