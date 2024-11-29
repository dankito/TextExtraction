package net.dankito.text.extraction.pdf

import io.github.jonathanlink.PDFLayoutTextStripper
import net.dankito.text.extraction.ITextExtractor.Companion.TextExtractionQualityForUnsupportedFileType
import net.dankito.text.extraction.TextExtractorBase
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Metadata
import net.dankito.text.extraction.model.Page
import net.dankito.utils.os.OsHelper
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.slf4j.LoggerFactory
import java.io.File


open class PdfBoxPdfTextExtractor(
    protected val metadataExtractor: IPdfMetadataExtractor = PdfBoxPdfMetadataExtractor(),
    protected val osHelper: OsHelper = OsHelper()
): TextExtractorBase(), ISearchablePdfTextExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(PdfBoxPdfTextExtractor::class.java)
    }


    override val name = "PdfBox"

    override val isAvailable = osHelper.isRunningOnAndroid == false

    override val supportedFileTypes = listOf("pdf")

    override fun getTextExtractionQualityForFileType(file: File): Int {
        if (isFileTypeSupported(file)) {
            return 65 // TODO
        }

        return TextExtractionQualityForUnsupportedFileType
    }


    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        Loader.loadPDF(file).use { document ->
            val textStripper = PDFLayoutTextStripper()

            val extractedText = ExtractionResult(null, "application/pdf", getMetadata(document, file))
            val countPages = document.numberOfPages

            for (pageNum in 1..countPages) {
                try {
                    textStripper.startPage = pageNum
                    textStripper.endPage = pageNum

                    val text = textStripper.getText(document)

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

    protected open fun getMetadata(pdfDocument: PDDocument, file: File): Metadata? {
        return if (metadataExtractor is PdfBoxPdfMetadataExtractor) {
            metadataExtractor.extractMetadata(pdfDocument, file)
        }
        else {
            metadataExtractor.extractMetadata(file)
        }
    }

}