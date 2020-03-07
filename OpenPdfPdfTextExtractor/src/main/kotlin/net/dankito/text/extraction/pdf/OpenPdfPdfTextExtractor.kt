package net.dankito.text.extraction.pdf

import com.lowagie.text.pdf.PdfReader
import com.lowagie.text.pdf.parser.PdfTextExtractor
import net.dankito.text.extraction.TextExtractorBase
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Page
import org.slf4j.LoggerFactory
import java.io.File


open class OpenPdfPdfTextExtractor: TextExtractorBase() {

    companion object {
        private val log = LoggerFactory.getLogger(OpenPdfPdfTextExtractor::class.java)
    }


    override val isAvailable = true

    override val supportedFileTypes = listOf("pdf")

    override val textExtractionQuality = 70


    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        file.inputStream().use { inputStream ->
            PdfReader(inputStream).use { reader ->

                val textExtractor = PdfTextExtractor(reader)

                val countPages = reader.numberOfPages
                val extractedText = ExtractionResult()

                for (pageNum in 1..countPages) {
                    try {
                        val text = textExtractor.getTextFromPage(pageNum)
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
    }

}