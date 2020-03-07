package net.dankito.text.extraction.model


open class ExtractionResult(val error: ErrorInfo? = null) {

    protected val pagesField = mutableListOf<Page>()


    open val errorOccurred: Boolean
        get() = error != null

    open val couldExtractText: Boolean
        get() = errorOccurred == false && pages.isNotEmpty() && text.isNotBlank()


    open val pages: List<Page>
        get() = ArrayList(pagesField) // don't return pagesField as otherwise it would be manipulated on the outside of this class

    open val text: String
        get() = pages.joinToString("\n")


    open fun addPage(page: Page) {
        pagesField.add(page)
    }


    override fun toString(): String {
        if (errorOccurred) {
            return "Error: $error"
        }

        if (couldExtractText == false) {
            return "Error: No pages could get extracted"
        }

        return "Success: $text"
    }

}