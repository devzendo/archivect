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

package org.devzendo.archivect.sources

import org.scalatest.junit.{ AssertionsForJUnit, MustMatchersForJUnit }
import org.junit.{ Test }
import org.devzendo.archivect.sources.Sources._
import org.devzendo.archivect.sources.SourceFactory._

class TestSources extends AssertionsForJUnit with MustMatchersForJUnit {

    @Test
    def newSourcesHaveNoSourcesAndNoRoots() {
        val sources = new Sources()
        sources.getRoots().size must be(0)
        sources.getSources().size() must be(0)

    }
}
