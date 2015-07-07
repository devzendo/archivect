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
  
package org.devzendo.archivect.filesystemaccess

import scala.collection.mutable.ListBuffer
import scala.util.parsing.combinator._
import org.scalatest.junit.{ AssertionsForJUnit, MustMatchersForJUnit }
import org.junit.Assert._
import org.junit.Test
import org.junit.Before
import org.easymock.EasyMock
import org.easymock.EasyMock._
import org.devzendo.xpfsa.FileSystemAccess

class TestFileSystemAccessFactory extends AssertionsForJUnit with MustMatchersForJUnit {
    val fsaf = new FileSystemAccessFactory()
    
    @Test
    def factoryIsEmptyInitially() {
        fsaf.getObject() must be (null)
    }
    
    @Test
    def factoryCanBePopulated() {
       val fsa = EasyMock.createMock(classOf[FileSystemAccess])
       fsa must not be (null)
       fsaf.setFileSystemAccess(fsa)
       fsaf.getObject() must be theSameInstanceAs (fsa)
    }
}