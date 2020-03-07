package net.dankito.text.extraction.pdf

import com.lowagie.text.pdf.PdfReader
import com.lowagie.text.pdf.parser.PdfTextExtractor
import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.model.ErrorInfo
import net.dankito.text.extraction.model.ErrorType
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Page
import org.slf4j.LoggerFactory
import java.io.File


class OpenPdfPdfTextExtractor: ITextExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(OpenPdfPdfTextExtractor::class.java)
    }


    override val isAvailable = true

    override val textExtractionQuality = 70

    override fun canExtractDataFromFile(file: File): Boolean {
        return "pdf" == file.extension.toLowerCase()
    }


    override fun extractText(file: File): ExtractionResult {
        try {
            val reader = PdfReader(file.inputStream())

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
        } catch (e: Exception) {
            log.error("Could not extract text of PDF '$file'", e)

            return ExtractionResult(ErrorInfo(ErrorType.ParseError, e))
        }
    }

}