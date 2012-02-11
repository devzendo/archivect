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

class SourceFactory {

}

object SourceFactory {
    val UNIX_SEPARATOR = '/'
    val WINDOWS_SEPARATOR = '\\'

    sealed abstract class Source(val path: String, val pathSeparatorChar: Char) {
        val pathComponents: List[String] = path.split("""[/\\]""").filter(component => component.length() > 0).toList
    }
    case class UnrootedSource(override val path: String, override val pathSeparatorChar: Char) extends Source(path, pathSeparatorChar)
    case class RootedSource(override val path: String, override val pathSeparatorChar: Char, root: String) extends Source(path, pathSeparatorChar)
    case class WindowsDriveSource(override val path: String, driveLetter: String) extends RootedSource(path, '\\', driveLetter)
    // Not sure I want to support UNC paths as sources
    case class UNCSource(override val path: String, override val root: String, server: String, share: String) extends RootedSource(path, '\\', root)
    
    private[this] val drivePath = """^(\S):([/\\])?(.*)$""".r // drive paths are absolute anyway, ignore leading \
    private[this] val uncPath = """^[/\\]{2}(.+?)[/\\](.+?)([/\\].*)?$""".r
    private[this] val rootedPath = """^([/\\].*)$""".r

    /**
     * Convert a path given a path separator
     *
     * @param inputPath a path to convert into a Source
     * @param pathSeparatorChar the separator to convert the path to use (for use in unrooted paths)
     * @return an appropriate Source
     */
    def _pathToSource(inputPath: String, pathSeparatorChar: Char): Source = {
        val trimmedPath = inputPath.trim
        trimmedPath match {
            case drivePath(driveLetter, ignoreLeadingSlash, path) =>
                WindowsDriveSource(_convertSlashes(removeLeading(nullToEmpty(path), "\\"), WINDOWS_SEPARATOR), endWith(driveLetter.toUpperCase, ":"))

            case uncPath(server, share, path) =>
                UNCSource(_convertSlashes(_removeLeadingSlashes(nullToEmpty(path)), WINDOWS_SEPARATOR), "\\\\" + server + "\\" + share, server, share)

            case rootedPath(path) =>
                RootedSource(_convertSlashes(path, pathSeparatorChar), pathSeparatorChar, pathSeparatorChar.toString)

            case path =>
                UnrootedSource(_convertSlashes(path, pathSeparatorChar), pathSeparatorChar)
        }
    }

    /**
     * Convert a path using the platform path separator for unrooted paths
     *
     * @param inputPath a path to convert into a Source
     * @return an appropriate Source
     */
    def pathToSource(inputPath: String): Source = {
        _pathToSource(inputPath, java.io.File.separatorChar)
    }
    
    private[this] def isSlash(c: Char): Boolean = {
        (c == '/' || c == '\\')
    }
    
    def _convertSlashes(path: String, pathSeparatorChar: Char): String = {
        path.map((c: Char) => if (isSlash(c)) pathSeparatorChar else c)
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

    def _removeLeadingSlashes(string: String): String = {
        val out = new StringBuilder
        var copy = false
        string.foreach((c: Char) => {
            val slash = isSlash(c)
            if (!slash) { copy = true }
            if (copy) {
                out += c
            }
        })
        out.toString()
    }

    private[this] def nullToEmpty(in: String): String = {
        if (in == null) "" else in
    }
}
