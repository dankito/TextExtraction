package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ITextExtractor


class iTextPdfTextExtractorTest : PdfTextExtractorTestBase() {

    override fun createExtractor(): ITextExtractor {
        return iTextPdfTextExtractor()
    }

}