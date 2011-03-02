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

import scala.collection.mutable.ListBuffer
import scala.xml.{Document, XML, Elem}
import java.io.{File, FileWriter}

object DefaultDestinations {
    private val LOGGER = Logger.getLogger(classOf[DefaultDestinations])
}

class DefaultDestinations(val destinationsPath: String) extends Destinations {
    private val destinationsFile = new File(destinationsPath)
    private val destinations = ListBuffer.empty[Destination] 
    
    createDestinationsIfStorageDoesNotExist()
    loadDestinations()
    
    def size() = destinations.size
    
    def getDestination(index: Int) = {
        destinations(index)
    }
    
    private def createDestinationsIfStorageDoesNotExist() = {
        if (!destinationsFile.exists()) {
            DefaultDestinations.LOGGER.debug("Creating initial destinations file")
            val doc =  <destinations/>
            xml.XML.save(destinationsPath, doc, "UTF-8", true)
            DefaultDestinations.LOGGER.debug("Created")
        }
    }
    
    private def loadDestinations() = {
        destinations.clear()
        val destinationsNode = XML.loadFile(destinationsPath)
        
    }
}