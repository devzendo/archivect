package org.devzendo.archivect.model2finder

import org.devzendo.archivect.model.CommandModel
import org.devzendo.archivect.finder.Finder
import org.devzendo.archivect.sources.SourceFactory._
import org.devzendo.archivect.rule.RuleCompiler
import org.devzendo.archivect.sources.{SourceFactory, SourcesRegistry}

class FinderInitialiser(ruleCompiler: RuleCompiler, finder: Finder) {
    def populateFromModel(model: CommandModel) {

        /*
        // TODO validate each source in the model, it must be a directory,
        // not a file. Throw if it's a file. The finder only copes with
        // sources that are directories.
        // TODO write tests for this
        val sourcesRegistry = new SourcesRegistry() // TODO inject?
        for (sourceString <- model.sources) {
            val source = SourceFactory.pathToSource(sourceString)
            // TODO how to tie up the source to rules that apply to it?
            sourcesRegistry._addSource(source)
        }
        for (exclusionString <- model.exclusions) {
            val exclusion = SourceFactory.pathToSource(exclusionString)
            // TODO dammit, can be a glob
            // TODO convert to a regex/glob exclusion rule at the parent
            // directory?

        }
        for (incRule <- model.includeRules) {
            val incRuleSource = SourceFactory.pathToSource(incRule.ruleAt)
            // TODO compile rule?
            // TODO get the sourceTree from the registry that matches this rule
            // TODO add the compiled rule into the sourceTree at the
            // incRuleSource point
        }
        for (excRule <- model.excludeRules) {
            val excRuleSource = SourceFactory.pathToSource(excRule.ruleAt)
            // TODO compile rule?
            // TODO get the sourceTree from the registry that matches this rule
            // TODO add the compiled rule into the sourceTree at the
            // excRuleSource point
        }
        */
    }
}