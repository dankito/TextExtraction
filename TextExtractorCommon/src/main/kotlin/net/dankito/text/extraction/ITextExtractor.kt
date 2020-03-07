package net.dankito.text.extraction

import net.dankito.text.extraction.model.ExtractionResult
import java.io.File


interface ITextExtractor {

    val isAvailable: Boolean

    val supportedFileTypes: List<String>

    fun isFileTypeSupported(file: File): Boolean {
        return supportedFileTypes.contains(file.extension.toLowerCase())
    }

    val textExtractionQuality: Int

    fun canExtractDataFromFile(file: File): Boolean {
        return isFileTypeSupported(file)
    }

    fun extractText(file: File): ExtractionResult

}