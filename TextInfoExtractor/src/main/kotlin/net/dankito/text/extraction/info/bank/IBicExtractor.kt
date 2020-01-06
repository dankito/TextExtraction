package net.dankito.text.extraction.info.bank

import net.dankito.text.extraction.info.model.StringSearchResult


interface IBicExtractor {

    fun extractBics(text: String): List<StringSearchResult>

    fun extractBics(lines: List<String>): List<StringSearchResult>

}