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
package org.devzendo.archivect.rule

import org.devzendo.xpfsa.{ DetailedFile, UnixFileStatus }

sealed abstract class CompositeDetailedFileMatcher extends DetailedFileMatcher {

}

class PassDetailedFileMatcher extends CompositeDetailedFileMatcher {
    def matches(file: DetailedFile): Boolean = {
        true
    }
}

class FailDetailedFileMatcher extends CompositeDetailedFileMatcher {
    def matches(file: DetailedFile): Boolean = {
        false
    }
}

class NotDetailedFileMatcher(orig: DetailedFileMatcher) extends CompositeDetailedFileMatcher {
    def matches(file: DetailedFile): Boolean = {
        ! orig.matches(file)
    }
}

class OrDetailedFileMatcher(left: DetailedFileMatcher, right: DetailedFileMatcher) extends CompositeDetailedFileMatcher {
    def matches(file: DetailedFile): Boolean = {
        left.matches(file) || right.matches(file)
    }
}