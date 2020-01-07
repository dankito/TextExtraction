package net.dankito.text.extraction

import java.io.File


interface ITextExtractorRegistry {

    fun findBestExtractor(file: File): ITextExtractor?

}