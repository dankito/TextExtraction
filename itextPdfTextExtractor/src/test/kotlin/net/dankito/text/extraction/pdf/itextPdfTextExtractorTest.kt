package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ITextExtractor


class itextPdfTextExtractorTest : PdfTextExtractorTestBase() {

    override fun createExtractor(): ITextExtractor {
        return itextPdfTextExtractor()
    }

}