package net.dankito.text.extraction

import net.dankito.text.extraction.ITextExtractor.Companion.TextExtractionQualityForUnsupportedFileType
import net.dankito.text.extraction.model.ExtractionResult
import java.io.File


open class FindBestTextExtractor(protected val extractorRegistry: ITextExtractorRegistry) : TextExtractorBase() {

    override val isAvailable = true

    override val supportedFileTypes: List<String>
        get() = extractorRegistry.extractors.flatMap { it.supportedFileTypes }.toSet().toList()


    override fun getTextExtractionQualityForFileType(file: File): Int {
        return extractorRegistry.findBestExtractorForFile(file)?.getTextExtractionQualityForFileType(file)
            ?: TextExtractionQualityForUnsupportedFileType
    }

    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        return extractorRegistry.extractTextWithBestExtractorForFile(file)
    }

}