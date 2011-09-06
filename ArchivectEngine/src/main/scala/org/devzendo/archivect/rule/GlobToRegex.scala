package org.devzendo.archivect.rule

import scala.util.matching.Regex

object GlobToRegex {
    def globAsRegex(glob: String) = {
        val regexStr = new StringBuilder()
        regexStr += '^'
        
        glob.foreach(ch => {
            ch match {
                case '.' =>
                    regexStr append """\."""
                case '*' =>
                    regexStr append """.*"""
                case '?' =>
                    regexStr append """."""
                case _ =>
                    regexStr append ch
            }
        })
        
        regexStr += '$'
        new Regex(regexStr.toString)
    }
}