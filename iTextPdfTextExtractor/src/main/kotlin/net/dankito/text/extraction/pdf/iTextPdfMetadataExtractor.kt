package net.dankito.text.extraction.pdf

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import net.dankito.text.extraction.model.Metadata
import org.slf4j.LoggerFactory
import java.io.File


open class iTextPdfMetadataExtractor : IPdfMetadataExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(iTextPdfMetadataExtractor::class.java)
    }


    override fun extractMetadata(file: File): Metadata? {
        file.inputStream().use { inputStream ->
            PdfReader(inputStream).use { reader ->
                val pdfDocument = PdfDocument(reader)

                return extractMetadata(pdfDocument, file)
            }
        }
    }

    open fun extractMetadata(pdfDocument: PdfDocument, file: File): Metadata? {
        try {
            val title = pdfDocument.documentInfo.title
            val author = pdfDocument.documentInfo.author
            val keywords = pdfDocument.documentInfo.keywords

            return Metadata(title, author, pdfDocument.numberOfPages, keywords = keywords)
        } catch (e: Exception) {
            log.error("Could not extract metadata of file $file", e)
        }

        return null
    }

}