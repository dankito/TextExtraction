package net.dankito.text.extraction

import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Page
import java.io.File


/**
 * Simply tries to read the plain text from a file.
 */
open class PlainTextFileTextExtractor : TextExtractorBase() {

    override val name = "Plain Text" // TODO: translate

    override val isAvailable = true

    override val supportedFileTypes = listOf("txt", "csv", "yml", "yaml", "json", "js", "css",
        "kt", "java", "gradle", "gradle.kts", "properties", "gitignore", "md", "asciidoc", "adoc", "sh", "bat", "sql", "php", "log")

    override fun getTextExtractionQualityForFileType(file: File): Int {
        return 100
    }


    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        val content = file.readText()

        // TODO: add programming language as Metadata.language?
        return ExtractionResult(null, null, null, listOf(Page(content))) // TODO: set mime type
    }

}