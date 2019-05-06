package net.dankito.text.extraction.pdf

import java.io.File
import com.lowagie.text.pdf.PdfReader
import com.lowagie.text.pdf.parser.PdfTextExtractor


class PdfTextExtractor {

    fun extractText(pdfFile: File): String {
        val reader = PdfReader(pdfFile.inputStream())

        val textExtractor = PdfTextExtractor(reader)
        val extractedText = StringBuilder()

        for (pageNum in 1..reader.numberOfPages) {
            extractedText.appendln(textExtractor.getTextFromPage(pageNum))
        }

        reader.close()

        return extractedText.toString()
    }

}