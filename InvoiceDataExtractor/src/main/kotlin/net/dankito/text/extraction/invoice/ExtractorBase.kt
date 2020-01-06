package net.dankito.text.extraction.invoice

import net.dankito.text.extraction.invoice.model.StringSearchResult
import java.util.regex.Pattern


abstract class ExtractorBase {

    protected open fun getLines(text: String): List<String> {
        return text.split("\n")
    }

    protected open fun findStrings(lines: List<String>, pattern: Pattern): List<StringSearchResult> {
        val matchers = lines.associateBy({ it }, { pattern.matcher(it) })

        val matches = matchers.filter { it.value.find() }

        return matches.map { StringSearchResult(it.value.group(), it.key) }
    }

}