package net.dankito.text.extraction.pdf

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import net.dankito.text.extraction.ITextExtractor.Companion.TextExtractionQualityForUnsupportedFileType
import net.dankito.text.extraction.TextExtractorBase
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Metadata
import net.dankito.text.extraction.model.Page
import org.slf4j.LoggerFactory
import java.io.File


open class itextPdfTextExtractor(
    protected val metadataExtractor: IPdfMetadataExtractor = itextPdfMetadataExtractor()
): TextExtractorBase(), ISearchablePdfTextExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(itextPdfTextExtractor::class.java)
    }


    override val name = "iText"

    override val isAvailable = true // TODO: is this true for all Android versions?

    override val supportedFileTypes = listOf("pdf")

    override fun getTextExtractionQualityForFileType(file: File): Int {
        if (isFileTypeSupported(file)) {
            return 95
        }

        return TextExtractionQualityForUnsupportedFileType
    }


    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        file.inputStream().use { inputStream ->
            PdfReader(inputStream).use { reader ->
                val pdfDocument = PdfDocument(reader)

                val extractedText = ExtractionResult(null, "application/pdf", getMetadata(pdfDocument, file))
                val countPages = pdfDocument.numberOfPages

                for (pageNum in 1..countPages) {
                    try {
                        val page = pdfDocument.getPage(pageNum)
                        val text = PdfTextExtractor.getTextFromPage(page)

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

    protected open fun getMetadata(pdfDocument: PdfDocument, file: File): Metadata? {
        return if (metadataExtractor is itextPdfMetadataExtractor) {
            metadataExtractor.extractMetadata(pdfDocument, file)
        }
        else {
            metadataExtractor.extractMetadata(file)
        }
    }

}