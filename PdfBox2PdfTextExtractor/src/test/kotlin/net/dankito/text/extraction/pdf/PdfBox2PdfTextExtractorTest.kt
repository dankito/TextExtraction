package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ITextExtractor


class PdfBox2PdfTextExtractorTest : PdfTextExtractorTestBase() {

    override fun createExtractor(): ITextExtractor {
        return PdfBox2PdfTextExtractor()
    }

}