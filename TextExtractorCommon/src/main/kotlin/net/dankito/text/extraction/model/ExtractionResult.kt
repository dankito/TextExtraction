package net.dankito.text.extraction.model


open class ExtractionResult(val error: ErrorInfo? = null) {

    protected val pagesField = mutableListOf<Page>()


    open val pages: List<Page>
        get() = ArrayList(pagesField) // don't return pagesField as otherwise it would be manipulated on the outside of this class

    open val text: String
        get() = pages.joinToString("\n")


    open fun addPage(page: Page) {
        pagesField.add(page)
    }


    override fun toString(): String {
        return text
    }

}