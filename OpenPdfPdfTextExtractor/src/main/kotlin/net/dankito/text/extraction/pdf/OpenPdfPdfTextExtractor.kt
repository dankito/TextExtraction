package net.dankito.text.extraction.pdf

import com.lowagie.text.pdf.PdfReader
import com.lowagie.text.pdf.parser.PdfTextExtractor
import net.dankito.text.extraction.ITextExtractor.Companion.TextExtractionQualityForUnsupportedFileType
import net.dankito.text.extraction.TextExtractorBase
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Metadata
import net.dankito.text.extraction.model.Page
import org.slf4j.LoggerFactory
import java.io.File


open class OpenPdfPdfTextExtractor(
    protected val metadataExtractor: IPdfMetadataExtractor = OpenPdfPdfMetadataExtractor()
): TextExtractorBase(), ISearchablePdfTextExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(OpenPdfPdfTextExtractor::class.java)
    }


    override val name = "OpenPdf"

    override val isAvailable = true

    override val supportedFileTypes = listOf("pdf")

    override fun getTextExtractionQualityForFileType(file: File): Int {
        if (isFileTypeSupported(file)) {
            return 70
        }

        return TextExtractionQualityForUnsupportedFileType
    }


    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        file.inputStream().use { inputStream ->
            PdfReader(inputStream).use { reader ->

                val textExtractor = PdfTextExtractor(reader)

                val countPages = reader.numberOfPages
                val extractedText = ExtractionResult(null, "application/pdf", getMetadata(reader, file))

                for (pageNum in 1..countPages) {
                    try {
                        val text = textExtractor.getTextFromPage(pageNum)
                        extractedText.addPage(Page(text, pageNum))

                        log.debug("Extracted text of page $pageNum / $countPages")
                    } catch (e: Exception) {
                        log.error("Could not extract page $pageNum of $file", e)

                        extractedText.addPage(Page("", pageNum)) // add empty page
                    }
                }

                return extractedText
            }
        }
    }

    protected open fun getMetadata(reader: PdfReader, file: File): Metadata? {
        return if (metadataExtractor is OpenPdfPdfMetadataExtractor) {
            metadataExtractor.extractMetadata(reader, file)
        }
        else {
            metadataExtractor.extractMetadata(file)
        }
    }

}