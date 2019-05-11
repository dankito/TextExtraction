package net.dankito.text.extraction.model


open class Page(val text: String, pageNum: Int = 1) {


    override fun toString(): String {
        return text
    }

}