package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ITextExtractor


class PdfBoxPdfTextExtractorTest : PdfTextExtractorTestBase() {

    override fun createExtractor(): ITextExtractor {
        return PdfBoxPdfTextExtractor()
    }

}