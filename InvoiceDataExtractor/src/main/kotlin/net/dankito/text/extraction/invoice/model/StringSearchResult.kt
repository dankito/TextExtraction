package net.dankito.text.extraction.invoice.model


open class StringSearchResult(val hit: String, foundInLine: String) : SearchResult(foundInLine) {

    override fun toString(): String {
        return "Found '$hit' in '$foundInLine'"
    }

}