package net.dankito.text.extraction

import java.io.File


open class TextExtractorRegistry @JvmOverloads constructor(extractors: List<ITextExtractor> = listOf()) : ITextExtractorRegistry {

    protected val availableExtractors: MutableList<ITextExtractor> = ArrayList(extractors)

    open val extractors: List<ITextExtractor>
        get() = ArrayList(availableExtractors)


    override fun findBestExtractor(file: File): ITextExtractor? {
        return extractors.sortedByDescending { it.textExtractionQuality }
            .firstOrNull { it.isAvailable && it.canExtractDataFromFile(file) }
    }


    open fun addExtractor(extractor: ITextExtractor): Boolean {
        return availableExtractors.add(extractor)
    }

    open fun removeExtractor(extractor: ITextExtractor): Boolean {
        return availableExtractors.remove(extractor)
    }

}