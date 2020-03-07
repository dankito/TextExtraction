package net.dankito.text.extraction

import net.dankito.text.extraction.model.ErrorInfo
import net.dankito.text.extraction.model.ErrorType
import net.dankito.text.extraction.model.ExtractionResult
import org.slf4j.LoggerFactory
import java.io.File


abstract class TextExtractorBase : ITextExtractor {

    private val log = LoggerFactory.getLogger(this.javaClass) // so that extractor's real class name gets logged


    abstract fun extractTextForSupportedFormat(file: File): ExtractionResult


    override fun extractText(file: File): ExtractionResult {
        if (isAvailable == false) {
            return ExtractionResult(ErrorInfo(ErrorType.ExtractorNotAvailable))
        }

        if (canExtractDataFromFile(file) == false) {
            return ExtractionResult(ErrorInfo(ErrorType.FileTypeNotSupportedByExtractor))
        }

        try {
            return extractTextForSupportedFormat(file)
        } catch (e: Exception) {
            log.error("Could not extract text of file '$file' with extractor ${javaClass.name}", e)

            return ExtractionResult(ErrorInfo(ErrorType.ParseError, e))
        }
    }

}