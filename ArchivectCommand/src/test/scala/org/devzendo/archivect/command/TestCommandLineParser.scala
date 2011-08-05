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

import scala.collection.mutable.ListBuffer
import org.scalatest.junit.{ AssertionsForJUnit, MustMatchersForJUnit }
import org.junit.Assert._
import org.junit.Test
import org.junit.Before
import org.devzendo.archivect.command.CommandModel.CommandMode._
import org.devzendo.archivect.command.CommandModel.Encoding._
import org.devzendo.archivect.command.CommandModel.Compression._
import org.junit.Ignore

class TestCommandLineParser extends AssertionsForJUnit with MustMatchersForJUnit {
    @Test
    def nonVerboseByDefault() {
        val model = parse("-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar") // -archive since a mode must be specified
        assertFalse(model.verbose)
    }

    @Test
    def verboseCanBeSpecified() {
        val model = parse("-archive -v irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar") // -archive since a mode must be specified
        assertTrue(model.verbose)
    }

    @Test
    def aModeMustBeSpecified() {
        val ex = intercept[CommandLineException] {
            parse("-irrelevant irrelevantsource -name irrelevant")
        }
        ex.getMessage() must equal("A mode must be specified")
    }

    @Test
    def allModesAreAccepted() {
        CommandModel.CommandMode.values.foreach {
            validMode =>
                val modeArgumentString = "-" + validMode.toString().toLowerCase()
                parse(modeArgumentString + " irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar").mode must equal(Some(validMode))
        }
    }
    
    @Test
    def cannotSpecifyMoreThanOneMode() {
        val ex = intercept[CommandLineException] {
            parse("-archive -restore irrelevantsource")
        }
        ex.getMessage() must equal("Cannot set the mode multiple times")
    }
    
    @Test
    def archiveModeMustHaveSources() {
        val ex = intercept[CommandLineException] {
            parse("-archive")
        }
        ex.getMessage() must equal("One or more sources must be specified")
    }

    @Test
    def backupModeMustHaveSources() {
        val ex = intercept[CommandLineException] {
            parse("-backup")
        }
        ex.getMessage() must equal("One or more sources must be specified")
    }
    
    @Test
    def sourcesAreAvailable() {
        val sources = parse("-archive sourceOne sourceTwo sourceThree -destination irrelevantdestination -name irrelevant -encoding tar").sources
        sources must have size(3)
        sources must contain("sourceOne")
        sources must contain("sourceTwo")
        sources must contain("sourceThree")
    }
    
    @Test
    def archiveModeMostHaveDestination() {
        val ex = intercept[CommandLineException] {
            parse("-archive sourceOne")
        }
        ex.getMessage() must equal("A destination must be specified")
    }
    
    @Test
    def cannotSpecifyDestinationMoreThanOnce() {
        val ex = intercept[CommandLineException] {
            parse("-archive -destination foo -destination bar irrelevantsource")
        }
        ex.getMessage() must equal("Cannot set the destination multiple times")
    }

    @Test
    def destinationMustNotBeFinalArgument() {
        val ex = intercept[CommandLineException] {
            parse("-archive irrelevantsource -destination")
        }
        ex.getMessage() must equal("A destination must be given, following -destination")
    }

    @Test
    def nameMustNotBeFinalArgument() {
        val ex = intercept[CommandLineException] {
            parse("-archive irrelevantsource -destination irrelevantdestination -name")
        }
        ex.getMessage() must equal("A name must be given, following -name")
    }

    @Test
    def archiveModeMostHaveName() {
        val ex = intercept[CommandLineException] {
            parse("-archive irrelevantsource -destination irrelevant")
        }
        ex.getMessage() must equal("A name must be specified")
    }
    
    @Test
    def cannotSpecifyNameMoreThanOnce() {
        val ex = intercept[CommandLineException] {
            parse("-archive -destination irrelevant -name foo -name bar irrelevantsource")
        }
        ex.getMessage() must equal("Cannot set the name multiple times")
    }

    @Test
    def encodingMustNotBeFinalArgument() {
        val ex = intercept[CommandLineException] {
            parse("-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding")
        }
        ex.getMessage() must equal("An encoding must be given, following -encoding")
    }

    @Test
    def archiveModeMostHaveEncoding() {
        val ex = intercept[CommandLineException] {
            parse("-archive irrelevantsource -destination irrelevant -name irrelevant")
        }
        ex.getMessage() must equal("An encoding must be specified")
    }
    
    @Test
    def cannotSpecifyEncodingMoreThanOnce() {
        val ex = intercept[CommandLineException] {
            parse("-archive -destination irrelevant -name irrelevant -encoding zip -encoding tar")
        }
        ex.getMessage() must equal("Cannot set the encoding multiple times")
    }

    @Test
    def unknownEncodingIsNotAllowed() {
        val ex = intercept[CommandLineException] {
            parse("-archive irrelevantsource -destination irrelevant -name irrelevant -encoding bar")
        }
        ex.getMessage() must equal("Unknown encoding 'bar'")
    }

    @Test
    def unknownCompressionIsNotAllowed() {
        val ex = intercept[CommandLineException] {
            parse("-archive irrelevantsource -destination irrelevant -name irrelevant -encoding tar.xx")
        }
        ex.getMessage() must equal("Unknown encoding 'tar.xx'")
    }

    @Test
    def validCombinationsOfEncoding()
    {
        // leading dot
        parseEncoding(".tar.gz", Some(CommandModel.Encoding.Tar), Some(CommandModel.Compression.Gzip))
        parseEncoding(".tar.bz", Some(CommandModel.Encoding.Tar), Some(CommandModel.Compression.Bzip))
        parseEncoding(".tgz", Some(CommandModel.Encoding.Tar), Some(CommandModel.Compression.Gzip))
        parseEncoding(".tbz", Some(CommandModel.Encoding.Tar), Some(CommandModel.Compression.Bzip))
        parseEncoding(".zip", Some(CommandModel.Encoding.Zip), None)
        parseEncoding(".aar", Some(CommandModel.Encoding.Aar), None)
        // and without
        parseEncoding("tar.gz", Some(CommandModel.Encoding.Tar), Some(CommandModel.Compression.Gzip))
        parseEncoding("tar.bz", Some(CommandModel.Encoding.Tar), Some(CommandModel.Compression.Bzip))
        parseEncoding("tgz", Some(CommandModel.Encoding.Tar), Some(CommandModel.Compression.Gzip))
        parseEncoding("tbz", Some(CommandModel.Encoding.Tar), Some(CommandModel.Compression.Bzip))
        parseEncoding("zip", Some(CommandModel.Encoding.Zip), None)
        parseEncoding("aar", Some(CommandModel.Encoding.Aar), None)
        // Case insensitivity (incomplete)
        parseEncoding("Aar", Some(CommandModel.Encoding.Aar), None)
        parseEncoding(".TaR.gZ", Some(CommandModel.Encoding.Tar), Some(CommandModel.Compression.Gzip))
    }

    @Test
    def cannotSpecifyCompressionMoreThanOnce() {
        val ex = intercept[CommandLineException] {
            parse("-archive -destination irrelevant -name foo irrelevantsource -encoding .tgz.bz")
        }
        ex.getMessage() must equal("Cannot set the compression multiple times")
    }

    @Test
    def zipIsInternallyCompressedAndCannotBeSpecified() {
        val ex = intercept[CommandLineException] {
            parse("-archive -destination irrelevant -name foo irrelevantsource -encoding .zip.bz")
        }
        ex.getMessage() must equal("Cannot set the compression for Zip encoding")
    }

    @Test
    def aarIsInternallyCompressedAndCannotBeSpecified() {
        val ex = intercept[CommandLineException] {
            parse("-archive -destination irrelevant -name foo irrelevantsource -encoding .aar.bz")
        }
        ex.getMessage() must equal("Cannot set the compression for Aar encoding")
    }

    @Test
    def noExclusionsIfNotSpecified() {
        val model = parse("-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar") // -archive since a mode must be specified
        model.exclusions.size must equal(0)
    }

    @Test
    def exclusionsCanBeSpecified() {
        val model = parse("-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -exclude /tmp/foo -exclude /bar/*.c")
        val xs = model.exclusions
        xs.size must equal(2)
        xs must contain("/tmp/foo")
        xs must contain("/bar/*.c")
    }

    @Test
    def excludeMustNotBeFinalArgument() {
        val ex = intercept[CommandLineException] {
            parse("-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -exclude")
        }
        ex.getMessage() must equal("An exclusion must be given, following -exclude")
    }
    
    @Test
    def excludeFromMustSupplyAnExistingFile() {
        val ex = intercept[CommandLineException] {
            parse("-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -excludefrom doesnotexist.txt")
        }
        ex.getMessage() must equal("The exclusion file 'doesnotexist.txt' does not exist")
    }

    @Test
    def excludeFromMustNotBeFinalArgument() {
        val ex = intercept[CommandLineException] {
            parse("-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -excludefrom")
        }
        ex.getMessage() must equal("An exclusion file must be given, following -excludefrom")
    }

    @Test
    def exclusionsCanBeSpecifiedViaAFile() {
        val model = parse("-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -exclude /tmp/foo -excludefrom src/test/resources/org/devzendo/archivect/command/exclusions.txt")
        val xs = model.exclusions
        xs.size must equal(4)
        xs must contain("/tmp/foo") // command line
        xs must contain("exclude1") // from file...
        xs must contain("/foo/*.txt")
        xs must contain("trimmed.filename")
    }

    private def parseEncoding(encString: String, enc: Option[Encoding], cmp: Option[Compression]) = {
        val cmd = "-archive irrelevantsource -destination irrelevant -name irrelevant -encoding "
        val model = parse(cmd + encString)
        model.encoding must equal(enc)
        model.compression must equal(cmp)
    }

    private def parse(line: String): CommandModel = {
        val parser = new CommandLineParser()
        parser.parse(split(line))
    }

    private def split(line: String): java.util.List[String] = {
        val wordList = new java.util.ArrayList[String]()
        line.split(" ") foreach { wordList.add(_) }
        wordList
    }
}