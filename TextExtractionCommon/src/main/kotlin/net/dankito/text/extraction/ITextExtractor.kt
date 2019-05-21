package net.dankito.text.extraction

import net.dankito.text.extraction.model.ExtractedText
import java.io.File


interface ITextExtractor {

    fun extractText(file: File): ExtractedText

}