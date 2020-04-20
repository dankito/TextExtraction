package net.dankito.text.extraction.pdf

import com.tom_roush.pdfbox.pdmodel.PDDocument
import net.dankito.text.extraction.model.Metadata
import org.slf4j.LoggerFactory
import java.io.File


open class PdfBoxAndroidPdfMetadataExtractor : IPdfMetadataExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(PdfBoxAndroidPdfMetadataExtractor::class.java)
    }


    override fun extractMetadata(file: File): Metadata? {
        PDDocument.load(file).use { document ->
            return extractMetadata(document, file)
        }
    }

    open fun extractMetadata(document: PDDocument, file: File): Metadata? {
        try {
            val info = document.documentInformation

            val title = info.title ?: ""
            val author = info.author ?: ""
            val keywords = info.keywords

            return Metadata(title, author, document.numberOfPages, keywords = keywords)
        } catch (e: Exception) {
            log.error("Could not extract metadata of file $file", e)
        }

        return null
    }

}