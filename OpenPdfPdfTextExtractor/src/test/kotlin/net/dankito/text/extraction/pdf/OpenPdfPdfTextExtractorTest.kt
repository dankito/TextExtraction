package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ITextExtractor


class OpenPdfPdfTextExtractorTest : PdfTextExtractorTestBase() {

    override fun createExtractor(): ITextExtractor {
        return OpenPdfPdfTextExtractor()
    }

}