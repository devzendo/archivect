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
  
package org.devzendo.archivect.rule

import org.devzendo.xpfsa.{ DetailedFile, FileStatus }

import java.io.File

object StubDetailedFile {
    def apply(fileName: String): StubDetailedFile = {
        val file = new File(fileName)
        StubDetailedFile(file, null)
    }
}

case class StubDetailedFile(val file: File, val fileStatus: FileStatus) extends DetailedFile {
    def getFile: File = file
    def getLinkDetailedFile: DetailedFile = null
    def getFileStatus: FileStatus = fileStatus
}