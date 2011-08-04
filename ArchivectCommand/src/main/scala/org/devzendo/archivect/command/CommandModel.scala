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

package org.devzendo.archivect.command

import scala.collection.mutable.ArrayBuffer

object CommandModel {
    object CommandMode extends Enumeration {
        type CommandMode = Value
        val Archive, Backup, Restore, Verify = Value
    }
    object Encoding extends Enumeration {
        type Encoding = Value
        val Tar, Zip, Aar = Value
    }
    object Compression extends Enumeration {
        type Compression = Value
        val Gzip, Bzip = Value
    }
}
import CommandModel.CommandMode._
import CommandModel.Encoding._
import CommandModel.Compression._

class CommandModel {
    var verbose: Boolean = false
    private[this] var _commandMode: Option[CommandMode] = None
    private[this] val _sources = new ArrayBuffer[String]()
    private[this] var _destination: Option[String] = None
    private[this] var _name: Option[String] = None
    private[this] var _encoding: Option[Encoding] = None
    private[this] var _compression: Option[Compression] = None
    
    def mode_=(newMode: CommandMode) = {
        if (_commandMode != None) {
            throw new IllegalStateException("Cannot set the mode multiple times")
        }
        _commandMode = Some(newMode)
    }
    
    def mode: Option[CommandMode] = _commandMode
    
    def addSource(source: String) = {
        _sources += source
    }
    
    def sources: List[String] = {
        _sources.toList
    }
    
    def destination_=(newDestination: String) = {
        if (_destination != None) {
            throw new IllegalStateException("Cannot set the destination multiple times")
        }
        _destination = Some(newDestination)
    }
    
    def destination: Option[String] = _destination

    def name_=(newName: String) = {
        if (_name != None) {
            throw new IllegalStateException("Cannot set the name multiple times")
        }
        _name = Some(newName)
    }
    
    def name: Option[String] = _name

    def encoding_=(newEncoding: Encoding) = {
        if (_encoding != None) {
            throw new IllegalStateException("Cannot set the encoding multiple times")
        }
        _encoding = Some(newEncoding)
    }
    
    def encoding: Option[Encoding] = _encoding

    def compression_=(newCompression: Compression) = {
        if (_encoding == Some(Zip) || _encoding == Some(Aar)) {
            throw new IllegalStateException("Cannot set the compression for " + _encoding.get.toString + " encoding")
        }
        if (_compression != None) {
            throw new IllegalStateException("Cannot set the compression multiple times")
        }
        _compression = Some(newCompression)
    }
    
    def compression: Option[Compression] = _compression

}