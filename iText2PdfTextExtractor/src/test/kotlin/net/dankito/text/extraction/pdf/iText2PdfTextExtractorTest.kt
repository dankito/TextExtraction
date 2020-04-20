package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ITextExtractor


class iText2PdfTextExtractorTest : PdfTextExtractorTestBase() {

    override fun createExtractor(): ITextExtractor {
        return iText2PdfTextExtractor()
    }

}