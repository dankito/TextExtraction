package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ITextExtractor
import org.jsoup.Jsoup


class pdfToHtmlPdfTextExtractorTest : PdfTextExtractorTestBase() {

    override fun createExtractor(): ITextExtractor {
        return pdfToHtmlPdfTextExtractor()
    }


    override fun normalizeWhitespace(toNormalize: CharSequence): String {
        val plainText = Jsoup.parse(toNormalize.toString()).text()

        return super.normalizeWhitespace(plainText)
    }

}