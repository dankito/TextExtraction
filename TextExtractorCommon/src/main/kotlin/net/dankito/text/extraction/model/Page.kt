package net.dankito.text.extraction.model


open class Page(val text: String, val pageNum: Int = -1) {

    val isPageNumSet = pageNum > 0


    override fun toString(): String {
        return text
    }

}