package net.dankito.text.extraction.info.bank

import net.dankito.text.extraction.info.model.StringSearchResult


interface IIbanExtractor {

    fun extractIbans(text: String): List<StringSearchResult>

    fun extractIbans(lines: List<String>): List<StringSearchResult>

}