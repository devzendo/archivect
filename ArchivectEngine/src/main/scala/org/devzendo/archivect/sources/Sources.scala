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

package org.devzendo.archivect.sources

class Sources {

}

object Sources {
    sealed abstract class Source(val path: String, val pathSeparator: String) {
        val pathComponents: List[String] = path.split("""[/\\]""").filter(component => component.length() > 0).toList
    }
    case class UnrootedSource(override val path: String, override val pathSeparator: String) extends Source(path, pathSeparator)
    case class RootedSource(override val path: String, override val pathSeparator: String) extends Source(path, pathSeparator)
    case class WindowsDriveSource(override val path: String, val driveLetter: String) extends Source(path, "\\")
    // Not sure I want to support UNC paths as sources
    case class UNCSource(override val path: String, val server: String, val share: String) extends Source(path, "\\")
    
    private[this] val drivePath = """^(\S):(\\)?(.*)$""".r // drive paths are absolute anyway, ignore leading \
    private[this] val uncPath = """^\\\\(.+?)\\(.+?)(\\.*)?$""".r
    private[this] val rootedPath = """^([/\\].*)$""".r
    
    def pathToSource(path: String): Source = {
        val trimmedPath = path.trim
        trimmedPath match {
            case drivePath(driveLetter, ignoreLeadingSlash, path) =>
                WindowsDriveSource(removeLeading(nullToEmpty(path), "\\"), endWith(driveLetter.toUpperCase(), ":"))
                
            case uncPath(server, share, path) =>
                UNCSource(removeLeading(nullToEmpty(path), "\\"), server, share)
                
            case rootedPath(path) =>
                RootedSource(toPlatformSlashes(path), java.io.File.separator)

            case path =>
                UnrootedSource(toPlatformSlashes(path), java.io.File.separator)
        }
    }
    
    private[this] def toPlatformSlashes(path: String): String = {
        path.replaceAll("""[/\\]""", java.io.File.separator)
    }
    
    private[this] def endWith(string: String, endWith: String): String = {
        if (string.endsWith(endWith)) {
            string
        } else {
            string + endWith
        }
    }
    
    private[this] def removeLeading(string: String, lead: String): String = {
        if (string.startsWith(lead)) {
            string.substring(lead.length)
        } else {
            string
        }
    }
    
    private[this] def nullToEmpty(in: String): String = {
        if (in == null) "" else in
    }
}
