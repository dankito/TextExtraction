package net.dankito.text.extraction.pdf

import java.io.File
import com.lowagie.text.pdf.PdfReader
import com.lowagie.text.pdf.parser.PdfTextExtractor
import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.model.ExtractedText
import net.dankito.text.extraction.model.Page
import org.slf4j.LoggerFactory


class OpenPdfPdfTextExtraction: ITextExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(OpenPdfPdfTextExtraction::class.java)
    }


    override val isAvailable = true


    override fun extractText(file: File): ExtractedText {
        val reader = PdfReader(file.inputStream())

        val textExtractor = PdfTextExtractor(reader)

        val countPages = reader.numberOfPages
        val extractedText = ExtractedText(countPages)

        for (pageNum in 1..countPages) {
            extractedText.addPage(Page(textExtractor.getTextFromPage(pageNum), pageNum))

            log.debug("Extracted text of page $pageNum / $countPages")
        }

        reader.close()

        return extractedText
    }

}