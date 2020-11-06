package net.dankito.text.extraction.pdf

import com.lowagie.text.pdf.PdfReader
import net.dankito.text.extraction.model.Metadata
import org.slf4j.LoggerFactory
import java.io.File


open class iText2PdfMetadataExtractor : IPdfMetadataExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(iText2PdfMetadataExtractor::class.java)
    }


    override fun extractMetadata(file: File): Metadata? {
        file.inputStream().use { inputStream ->
            val reader = PdfReader(inputStream)

            val result = extractMetadata(reader, file)

            reader.close()

            return result
        }

    }

    open fun extractMetadata(reader: PdfReader, file: File): Metadata? {
        try {
            val title = reader.info["Title"] as? String
            val author = reader.info["Author"] as? String
            val keywords = reader.info["Keywords"] as? String

            return Metadata(title, author, reader.numberOfPages, keywords = keywords)
        } catch (e: Exception) {
            log.error("Could not extract metadata of file $file", e)
        }

        return null
    }

}