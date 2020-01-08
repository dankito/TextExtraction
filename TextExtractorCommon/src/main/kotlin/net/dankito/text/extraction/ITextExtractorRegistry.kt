package net.dankito.text.extraction

import java.io.File


interface ITextExtractorRegistry {

    fun getAllExtractorsForFile(file: File): List<ITextExtractor>

    fun findBestExtractorForFile(file: File): ITextExtractor?

}