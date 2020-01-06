package net.dankito.text.extraction

import net.dankito.text.extraction.model.ExtractedText
import java.io.File


interface ITextExtractor {

    val isAvailable: Boolean

    val textExtractionQuality: Int

    fun canExtractDataFromFile(file: File): Boolean

    fun extractText(file: File): ExtractedText

}