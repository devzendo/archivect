package org.devzendo.archivect.model2finder

import org.devzendo.archivect.model.CommandModel
import org.devzendo.archivect.finder.Finder
import org.devzendo.archivect.rule.RuleCompiler

class FinderInitialiser(ruleCompiler: RuleCompiler, finder: Finder) {
    def populateFromModel(model: CommandModel) {
        // TODO Auto-generated method stub
        // TODO validate each source in the model, it must be a directory,
        // not a file. Throw if it's a file. The finder only copes with
        // sources that are directories.
    }
}