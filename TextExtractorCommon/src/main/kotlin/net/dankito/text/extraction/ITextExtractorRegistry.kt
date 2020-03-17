package net.dankito.text.extraction

import net.dankito.text.extraction.model.ExtractionResultForExtractor
import java.io.File


interface ITextExtractorRegistry {

    val extractors: List<ITextExtractor>

    fun getAllExtractorsForFile(file: File): List<ITextExtractor>

    fun findBestExtractorForFile(file: File): ITextExtractor?


    fun extractTextWithBestExtractorForFile(file: File): ExtractionResultForExtractor

    suspend fun extractTextWithBestExtractorForFileSuspendable(file: File): ExtractionResultForExtractor

}