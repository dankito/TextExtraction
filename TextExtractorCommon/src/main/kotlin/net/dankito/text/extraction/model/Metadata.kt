package net.dankito.text.extraction.model


open class Metadata(
    val title: String?,
    val author: String? = null,
    val length: Int? = null,
    val category: String? = null,
    val language: String? = null,
    val series: String? = null,
    val keywords: List<String> = listOf()
) {

    override fun toString(): String {
        return "$title: $author"
    }

}