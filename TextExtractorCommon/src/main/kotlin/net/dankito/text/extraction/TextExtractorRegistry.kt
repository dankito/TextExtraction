package net.dankito.text.extraction

import java.io.File


open class TextExtractorRegistry @JvmOverloads constructor(extractors: List<ITextExtractor> = listOf()) : ITextExtractorRegistry {

    protected val availableExtractors: MutableList<ITextExtractor> = ArrayList(extractors)

    open val extractors: List<ITextExtractor>
        get() = ArrayList(availableExtractors)


    override fun getAllExtractorsForFile(file: File): List<ITextExtractor> {
        return extractors
            .sortedByDescending { it.textExtractionQuality }
            .filter { canExtractDataFromFile(it, file) }
    }

    override fun findBestExtractorForFile(file: File): ITextExtractor? {
        return extractors
            .sortedByDescending { it.textExtractionQuality }
            .firstOrNull { canExtractDataFromFile(it, file) }
    }


    protected open fun canExtractDataFromFile(extractor: ITextExtractor, file: File): Boolean {
        return extractor.isAvailable && extractor.canExtractDataFromFile(file)
    }


    open fun addExtractor(extractor: ITextExtractor): Boolean {
        return availableExtractors.add(extractor)
    }

    open fun removeExtractor(extractor: ITextExtractor): Boolean {
        return availableExtractors.remove(extractor)
    }

}