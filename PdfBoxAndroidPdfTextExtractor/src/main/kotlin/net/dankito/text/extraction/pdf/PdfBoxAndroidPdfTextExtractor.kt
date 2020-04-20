package net.dankito.text.extraction.pdf

import android.content.Context
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader
import net.dankito.text.extraction.ITextExtractor.Companion.TextExtractionQualityForUnsupportedFileType
import net.dankito.text.extraction.TextExtractorBase
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Metadata
import net.dankito.text.extraction.model.Page
import org.slf4j.LoggerFactory
import java.io.File


open class PdfBoxAndroidPdfTextExtractor(
    applicationContext: Context,
    protected val metadataExtractor: IPdfMetadataExtractor = PdfBoxAndroidPdfMetadataExtractor()
): TextExtractorBase(), ISearchablePdfTextExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(PdfBoxAndroidPdfTextExtractor::class.java)
    }


    override val name = "PdfBox Android"

    override val isAvailable = true

    override val supportedFileTypes = listOf("pdf")

    override fun getTextExtractionQualityForFileType(file: File): Int {
        if (isFileTypeSupported(file)) {
            return 65 // TODO
        }

        return TextExtractionQualityForUnsupportedFileType
    }


    init {
        PDFBoxResourceLoader.init(applicationContext)
    }


    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        PDDocument.load(file).use { document ->
            val textStripper = PDFTextStripper()

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
        return if (metadataExtractor is PdfBoxAndroidPdfMetadataExtractor) {
            metadataExtractor.extractMetadata(pdfDocument, file)
        }
        else {
            metadataExtractor.extractMetadata(file)
        }
    }

}