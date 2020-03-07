package net.dankito.text.extraction.pdf

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Page
import org.slf4j.LoggerFactory
import java.io.File


class itextPdfTextExtractor: ITextExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(itextPdfTextExtractor::class.java)
    }


    override val isAvailable = true // TODO: is this true for all Android versions?

    override val textExtractionQuality = 95

    override fun canExtractDataFromFile(file: File): Boolean {
        return "pdf" == file.extension.toLowerCase()
    }


    override fun extractText(file: File): ExtractionResult {
        val reader = PdfReader(file.inputStream())
        val pdfDocument = PdfDocument(reader)

        val countPages = pdfDocument.numberOfPages
        val extractedText = ExtractionResult()

        for (pageNum in 1..countPages) {
            try {
                val page = pdfDocument.getPage(pageNum)
                val text = PdfTextExtractor.getTextFromPage(page)

                extractedText.addPage(Page(text, pageNum))

                log.debug("Extracted text of page $pageNum / $countPages")
            } catch (e: Exception) {
                log.error("Could not extract page $pageNum of $file", e)
            }
        }

        reader.close()

        return extractedText
    }

}