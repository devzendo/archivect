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
import org.devzendo.archivect.model.{ CommandModel, Rule }
import org.devzendo.archivect.model.CommandModel.CommandMode._
import org.devzendo.archivect.model.CommandModel.Encoding._
import org.devzendo.archivect.model.CommandModel.Compression._
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
        commandFailsWithMessage(
            "-irrelevant irrelevantsource -name irrelevant",
            "A mode must be specified")
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
        commandFailsWithMessage(
            "-archive -restore irrelevantsource",
            "Cannot set the mode multiple times")
    }
    
    @Test
    def archiveModeMustHaveSources() {
        commandFailsWithMessage(
            "-archive",
            "One or more sources must be specified")
    }

    @Test
    def backupModeMustHaveSources() {
        commandFailsWithMessage(
            "-backup",
            "One or more sources must be specified")
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
        commandFailsWithMessage(
            "-archive sourceOne", 
            "A destination must be specified")
    }
    
    @Test
    def cannotSpecifyDestinationMoreThanOnce() {
        commandFailsWithMessage(
            "-archive -destination foo -destination bar irrelevantsource",
            "Cannot set the destination multiple times")
    }

    @Test
    def destinationMustNotBeFinalArgument() {
        commandFailsWithMessage(
            "-archive irrelevantsource -destination",
            "A destination must be given, following -destination")
    }

    @Test
    def nameMustNotBeFinalArgument() {
        commandFailsWithMessage(
            "-archive irrelevantsource -destination irrelevantdestination -name",
            "A name must be given, following -name")
    }

    @Test
    def archiveModeMostHaveName() {
        commandFailsWithMessage(
            "-archive irrelevantsource -destination irrelevant",
            "A name must be specified")
    }
    
    @Test
    def cannotSpecifyNameMoreThanOnce() {
        commandFailsWithMessage(
            "-archive -destination irrelevant -name foo -name bar irrelevantsource",
            "Cannot set the name multiple times")
    }

    @Test
    def encodingMustNotBeFinalArgument() {
        commandFailsWithMessage(
            "-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding",
            "An encoding must be given, following -encoding")
    }

    @Test
    def archiveModeMostHaveEncoding() {
        commandFailsWithMessage(
            "-archive irrelevantsource -destination irrelevant -name irrelevant",
            "An encoding must be specified")
    }
    
    @Test
    def cannotSpecifyEncodingMoreThanOnce() {
        commandFailsWithMessage(
            "-archive -destination irrelevant -name irrelevant -encoding zip -encoding tar",
            "Cannot set the encoding multiple times")
    }

    @Test
    def unknownEncodingIsNotAllowed() {
        commandFailsWithMessage(
            "-archive irrelevantsource -destination irrelevant -name irrelevant -encoding bar",
            "Unknown encoding 'bar'")
    }

    @Test
    def unknownCompressionIsNotAllowed() {
        commandFailsWithMessage(
            "-archive irrelevantsource -destination irrelevant -name irrelevant -encoding tar.xx",
            "Unknown encoding 'tar.xx'")
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
        commandFailsWithMessage(
            "-archive -destination irrelevant -name foo irrelevantsource -encoding .tgz.bz",
            "Cannot set the compression multiple times")
    }

    @Test
    def zipIsInternallyCompressedAndCannotBeSpecified() {
        commandFailsWithMessage(
            "-archive -destination irrelevant -name foo irrelevantsource -encoding .zip.bz",
            "Cannot set the compression for Zip encoding")
    }

    @Test
    def aarIsInternallyCompressedAndCannotBeSpecified() {
        commandFailsWithMessage(
            "-archive -destination irrelevant -name foo irrelevantsource -encoding .aar.bz",
            "Cannot set the compression for Aar encoding")
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
        commandFailsWithMessage(
            "-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -exclude",
            "An exclusion must be given, following -exclude")
    }
    
    @Test
    def excludeFromMustSupplyAnExistingFile() {
        commandFailsWithMessage(
            "-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -excludefrom doesnotexist.txt",
            "The exclusions file 'doesnotexist.txt' does not exist")
    }

    @Test
    def excludeFromMustNotBeFinalArgument() {
        commandFailsWithMessage(
            "-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -excludefrom",
            "An exclusions file must be given, following -excludefrom")
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

    @Test
    def noRulesIfNotSpecified() {
        val model = parse("-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar") // -archive since a mode must be specified
        model.includeRules.size must equal(0)
        model.excludeRules.size must equal(0)
    }

    @Test
    def rulesCanBeSpecified() {
        val model = parse("-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -rule glob *.obj,*.o,*.exe,*.a,*.so,*.dll /some/subfolder +rule glob *.cpp,*.h /some/subfolder")
        // parsing of rules is done in more detail in TestRuleParser,
        // this is just to check they are obtainable in the CommandModel
        val excs = model.excludeRules
        excs.size must equal(1)
        excs(0).ruleType must equal(CommandModel.RuleType.Glob)
        excs(0).ruleText must equal("*.obj,*.o,*.exe,*.a,*.so,*.dll")
        excs(0).ruleAt must equal("/some/subfolder")
        val incs = model.includeRules
        incs.size must equal(1)
        incs(0).ruleType must equal(CommandModel.RuleType.Glob)
        incs(0).ruleText must equal("*.cpp,*.h")
        incs(0).ruleAt must equal("/some/subfolder")
    }

    @Test
    def ruleExcludeMustNotBeFinalArgument() {
        commandFailsWithMessage(
            "-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -rule",
            "A rule exclusion must be of the form -rule <ruletype> <ruletext> <ruledirectory>")
    }

    @Test
    def ruleIncludeMustNotBeFinalArgument() {
        commandFailsWithMessage(
            "-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar +rule",
            "A rule inclusion must be of the form +rule <ruletype> <ruletext> <ruledirectory>")
    }

    @Test
    def ruleTypeMustNotBeFinalArgument() {
        commandFailsWithMessage(
            "-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -rule glob",
            "A rule exclusion must be of the form -rule <ruletype> <ruletext> <ruledirectory>")
    }

    @Test
    def ruleTextMustNotBeFinalArgument() {
        commandFailsWithMessage(
            "-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -rule glob *.cpp",
            "A rule exclusion must be of the form -rule <ruletype> <ruletext> <ruledirectory>")
    }

    @Test
    def ruleFromMustSupplyAnExistingFile() {
        commandFailsWithMessage(
            "-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -rulefrom doesnotexist.txt",
            "The rules file 'doesnotexist.txt' does not exist")
    }

    @Test
    def ruleFromMustNotBeFinalArgument() {
        commandFailsWithMessage(
            "-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -rulefrom",
            "A rules file must be given, following -rulefrom")
    }

    @Test
    def unknownRuleTypeIsNotAllowed() {
        commandFailsWithMessage(
            "-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -rule GaRbage bleh /tmp/foo",
            "Unknown rule type 'garbage'")
    }

    @Test
    def rulesCanBeSpecifiedViaAFile() {
        val model = parse("-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -exclude /tmp/foo -rulefrom src/test/resources/org/devzendo/archivect/command/rules.txt")
        val excs = model.excludeRules
        excs.size must equal(1)
        excs(0).ruleType must equal(CommandModel.RuleType.Glob)
        excs(0).ruleText must equal("*.obj,*.o,*.exe,*.a,*.so,*.dll")
        excs(0).ruleAt must equal("/some/subfolder with a silly space/in the path")
        val incs = model.includeRules
        incs.size must equal(5)
        incs(0).ruleType must equal(CommandModel.RuleType.Glob)
        incs(0).ruleText must equal("*.cpp,*.h")
        incs(0).ruleAt must equal("/some/subfolder")
        incs(1).ruleType must equal(CommandModel.RuleType.Regex)
        incs(1).ruleText must equal("^foobar$")
        incs(1).ruleAt must equal("/")
        incs(2).ruleType must equal(CommandModel.RuleType.IRegex)
        incs(2).ruleText must equal("(?:master)?file")
        incs(2).ruleAt must equal("c:\\windows\\filespec")
        incs(3).ruleType must equal(CommandModel.RuleType.FileType)
        incs(3).ruleText must equal("f")
        incs(3).ruleAt must equal("/Users/matt/Documents")
        incs(4).ruleType must equal(CommandModel.RuleType.FileType)
        incs(4).ruleText must equal("l")
        incs(4).ruleAt must equal("/Users/matt")
    }

    @Test
    def rulesInTheFileMustHaveValidType() {
        commandFailsWithMessage("-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -exclude /tmp/foo -rulefrom src/test/resources/org/devzendo/archivect/command/badrule.txt",
            "Unknown rule type 'vagueness'")
    }

    @Test
    def rulesInTheFileMustHaveValidInclusionExclusionType() {
        commandFailsWithMessage("-archive irrelevantsource -destination irrelevantdestination -name irrelevant -encoding tar -exclude /tmp/foo -rulefrom src/test/resources/org/devzendo/archivect/command/badruleincexc.txt",
            "Unknown rule inclusion/exclusion type '%'")
    }

    private def commandFailsWithMessage(cmdLine: String, message: String) = {
        val ex = intercept[CommandLineException] {
            parse(cmdLine)
        }
        ex.getMessage() must equal(message)
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