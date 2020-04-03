package net.dankito.text.extraction.pdf

import com.lowagie.text.pdf.PdfReader
import net.dankito.text.extraction.model.Metadata
import org.slf4j.LoggerFactory
import java.io.File


open class OpenPdfPdfMetadataExtractor : IPdfMetadataExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(OpenPdfPdfMetadataExtractor::class.java)
    }


    override fun extractMetadata(file: File): Metadata? {
        file.inputStream().use { inputStream ->
            PdfReader(inputStream).use { reader ->
                return extractMetadata(reader, file)
            }
        }

    }

    open fun extractMetadata(reader: PdfReader, file: File): Metadata? {
        try {
            val title = reader.info["Title"]
            val author = reader.info["Author"]
            val keywords = reader.info["Keywords"]

            return Metadata(title, author, reader.numberOfPages, keywords = keywords)
        } catch (e: Exception) {
            log.error("Could not extract metadata of file $file", e)
        }

        return null
    }

}