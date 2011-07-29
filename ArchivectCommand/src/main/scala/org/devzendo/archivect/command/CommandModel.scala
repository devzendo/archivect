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
}
import CommandModel.CommandMode._
class CommandModel {
    var verbose: Boolean = false
    private[this] var _commandMode: Option[CommandMode] = None
    private[this] val _sources = new ArrayBuffer[String]()
    
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
}

