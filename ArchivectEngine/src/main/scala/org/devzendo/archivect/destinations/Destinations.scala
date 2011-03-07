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

import collection.JavaConversions._

sealed abstract class Destination(val name: String)

case class LocalDestination(override val name: String, val localPath: String)
    extends Destination(name)

case class SmbDestination(override val name: String, val server: String,
    val share: String, val userName: String, val password: String,
    val localPath: String) extends Destination(name)
    
case class DestinationSummary(val name: String, val destinationType: String)

trait Destinations {
    /**
     * @return the number of destinations currently stored
     */
    def size: Int
    
    /**
     * Obtain a Destination by index
     * @param index the index of the destination to retrieve
     * @return a Destination
     */
    def getDestination(index: Int): Destination
    
    /**
     * Add a destination to storage
     * @param dest a destination to add
     */
    def addDestination(dest: Destination): Unit
    
    /**
     * Get a summary of the destinations
     * @return a list of summaries
     */
    def summaries: List[DestinationSummary]
    
    // There may be a neater way of doing this...
    implicit def summariesAsList: java.util.List[DestinationSummary] = {
        summaries
    }
}