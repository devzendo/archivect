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
 
package org.devzendo.archivect.sources

import java.io.{ File, PrintStream, FileOutputStream }
import scala.collection.mutable.ListBuffer
import org.scalatest.junit.{ AssertionsForJUnit, MustMatchersForJUnit }
import org.junit.Assert._
import org.junit.{ Test, Before, After, Ignore }
import org.junit.rules.TemporaryFolder
import org.devzendo.archivect.model.{ CommandModel, Rule }
import org.devzendo.archivect.model.CommandModel.CommandMode._
import org.devzendo.archivect.model.CommandModel.Encoding._
import org.devzendo.archivect.model.CommandModel.Compression._
import org.easymock.EasyMock
import org.easymock.EasyMock._
import org.devzendo.xpfsa.{ DetailedFile, FileSystemAccess }

class TestSources extends AssertionsForJUnit with MustMatchersForJUnit {

}