package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ITextExtractor


class pdfToTextPdfTextExtractorTest : PdfTextExtractorTestBase() {

    override fun createExtractor(): ITextExtractor {
        return pdfToTextPdfTextExtractor()
    }

}