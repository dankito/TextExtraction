package net.dankito.text.extraction.model


open class ExtractedText(var countPages: Int = 1) {

    protected val pagesField = mutableListOf<Page>()


    open val pages: List<Page>
        get() = ArrayList(pagesField)

    open val text: String
        get() = pages.joinToString("\n")


    open fun addPage(page: Page) {
        pagesField.add(page)
    }


    override fun toString(): String {
        return text
    }

}