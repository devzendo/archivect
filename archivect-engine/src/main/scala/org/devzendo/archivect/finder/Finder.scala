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
 
package org.devzendo.archivect.finder

import java.io.File
import scala.collection.mutable.ListBuffer
import org.devzendo.xpfsa.{ DetailedFile, FileSystemAccess }

class Finder(val fsa: FileSystemAccess) {
    var sources = new ListBuffer[File]
    
    /**
     * Add a source directory to the Finder. The Finder will coalesce source
     * paths so that it starts finding at the highest point on a path (closest
     * to the filesystem root) if given multiple sources along the same path.
     * The list of coalesced sources can be obtained by getCoalescedSources().
     * @param source a directory to be coalesced into the list of sources to be
     * found by this Finder. 
     */
    def addSource(source: File) {
        // TODO validate that this is a directory
        sources += source
        // TODO coalescing: what if this source lies along the path of an existing source?
        // e.g. we already have
        // /a/b/c/d/e/f/
        // and we're given /a/b
        // or /a/b/c/d/e/f/g/h
        // need to do some coalescing here
    }
    
    /**
     * Return the coalesced sources as an immutable list
     * TODO this needs testing - see coalesce example
     */
    def getCoalescedSources {
        List.empty ++ sources
    }
    
    def find(callback: DetailedFile => Unit) {
        sources.foreach( source => findSource(source, callback))
    }
    
    private[this] def findSource(source: File, callback : DetailedFile => Unit) {
        val it = fsa.getDirectoryIterator(source)
        while (it.hasNext()) {
            val detailedFile = it.next();
            // TODO add exclusion and rule procesing here
            callback->(detailedFile)
        }
    }
}