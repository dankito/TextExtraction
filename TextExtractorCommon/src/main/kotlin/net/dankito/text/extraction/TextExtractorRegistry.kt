package net.dankito.text.extraction

import net.dankito.text.extraction.model.ErrorInfo
import net.dankito.text.extraction.model.ErrorType
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.ExtractionResultForExtractor
import java.io.File


open class TextExtractorRegistry @JvmOverloads constructor(extractors: List<ITextExtractor> = listOf()) : ITextExtractorRegistry {

    protected val availableExtractors: MutableList<ITextExtractor> = ArrayList(extractors)

    override val extractors: List<ITextExtractor>
        get() = ArrayList(availableExtractors)


    override fun getAllExtractorsForFile(file: File): List<ITextExtractor> {
        return extractors
            .sortedByDescending { it.getTextExtractionQualityForFileType(file) }
            .filter { canExtractDataFromFile(it, file) }
    }

    override fun findBestExtractorForFile(file: File): ITextExtractor? {
        return extractors
            .sortedByDescending { it.getTextExtractionQualityForFileType(file) }
            .firstOrNull { canExtractDataFromFile(it, file) }
    }


    override fun extractTextWithBestExtractorForFile(file: File): ExtractionResultForExtractor {
        var mostSuitableError: ExtractionResult? = null
        var mostSuitableErrorTextExtractor: ITextExtractor? = null

        getAllExtractorsForFile(file).forEach { extractor ->
            val extractionResult = extractor.extractText(file)

            if (extractionResult.couldExtractText) {
                return ExtractionResultForExtractor(extractor, extractionResult)
            }

            if (mostSuitableError == null || mostSuitableError?.error == null ||
                listOf(ErrorType.ExtractorNotAvailable, ErrorType.FileTypeNotSupportedByExtractor).contains(mostSuitableError?.error?.type)) {
                mostSuitableError = extractionResult
                mostSuitableErrorTextExtractor = extractor
            }
        }

        mostSuitableError?.let {
            return ExtractionResultForExtractor(mostSuitableErrorTextExtractor, it.error)
        }

        return ExtractionResultForExtractor(null, ErrorInfo(ErrorType.NoExtractorFoundForFileType))
    }

    // TODO: how to get rid of duplicated code?
    override suspend fun extractTextWithBestExtractorForFileSuspendable(file: File): ExtractionResultForExtractor {
        var mostSuitableError: ExtractionResult? = null
        var mostSuitableErrorTextExtractor: ITextExtractor? = null

        getAllExtractorsForFile(file).forEach { extractor ->
            val extractionResult = extractor.extractTextSuspendable(file)

            if (extractionResult.couldExtractText) {
                return ExtractionResultForExtractor(extractor, extractionResult)
            }

            if (mostSuitableError == null || mostSuitableError?.error == null ||
                listOf(ErrorType.ExtractorNotAvailable, ErrorType.FileTypeNotSupportedByExtractor).contains(mostSuitableError?.error?.type)) {
                mostSuitableError = extractionResult
                mostSuitableErrorTextExtractor = extractor
            }
        }

        mostSuitableError?.let {
            return ExtractionResultForExtractor(mostSuitableErrorTextExtractor, it.error)
        }

        return ExtractionResultForExtractor(null, ErrorInfo(ErrorType.NoExtractorFoundForFileType))
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