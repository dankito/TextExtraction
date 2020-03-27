package net.dankito.text.extraction.model

import org.slf4j.LoggerFactory


open class Metadata(
    val title: String?,
    val author: String? = null,
    val length: Int? = null,
    val category: String? = null,
    val language: String? = null,
    val series: String? = null,
    val keywords: List<String> = listOf()
) {

    companion object {

        private val log = LoggerFactory.getLogger(Metadata::class.java)

        fun splitKeywords(keywords: String?): List<String> {
            try {
                if (keywords != null) {
                    val splitByComma = splitKeywordsBy(keywords, ",")
                    val splitBySemicolon = splitKeywordsBy(keywords, ";")
                    val splitBySlash = splitKeywordsBy(keywords, "/")

                    if (splitBySemicolon.size > splitByComma.size && splitBySemicolon.size >= splitBySlash.size) {
                        return splitBySemicolon
                    }
                    else if (splitBySlash.size > splitByComma.size && splitBySlash.size >= splitBySemicolon.size) {
                        return splitBySlash
                    }

                    return splitByComma
                }
            } catch (e: Exception) {
                log.warn("Could not split keywords string '$keywords'", e)
            }

            return listOf()
        }

        fun splitKeywordsBy(keywords: String, separator: String): List<String> {
            return keywords.split(separator).filter { it.isNotBlank() }.map { it.trim() }
        }

    }

    constructor(title: String?, author: String? = null, length: Int? = null, category: String? = null,
                language: String? = null, series: String? = null, keywords: String? = null)
            : this(title, author, length, category, language, series, splitKeywords(keywords))

    override fun toString(): String {
        return "$title: $author"
    }

}