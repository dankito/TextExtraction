package net.dankito.text.extraction

import net.dankito.text.extraction.model.ErrorInfo
import net.dankito.text.extraction.model.ErrorType
import net.dankito.text.extraction.model.ExtractionResult
import org.slf4j.LoggerFactory
import java.io.File


abstract class TextExtractorBase : ITextExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(TextExtractorBase::class.java)
    }


    override val installHint = ""


    abstract fun extractTextForSupportedFormat(file: File): ExtractionResult

    // default implementation, may be overridden in subclasses with real suspendable functions
    open suspend fun extractTextForSupportedFormatSuspendable(file: File): ExtractionResult {
        return extractTextForSupportedFormat(file)
    }


    override fun extractText(file: File): ExtractionResult {
        checkIfCanNotExtractTextForFile(file)?.let { canNotExtractTextExtractionResult ->
            return canNotExtractTextExtractionResult
        }

        try {
            return extractTextForSupportedFormat(file)
        } catch (e: Exception) {
            log.error("Could not extract text of file '$file' with extractor ${javaClass.name}", e)

            return ExtractionResult(ErrorInfo(ErrorType.ParseError, e))
        }
    }

    override suspend fun extractTextSuspendable(file: File): ExtractionResult {
        checkIfCanNotExtractTextForFile(file)?.let { canNotExtractTextExtractionResult ->
            return canNotExtractTextExtractionResult
        }

        try {
            return extractTextForSupportedFormatSuspendable(file)
        } catch (e: Exception) {
            log.error("Could not extract text of file '$file' with extractor ${this@TextExtractorBase.javaClass.name}", e)

            return ExtractionResult(ErrorInfo(ErrorType.ParseError, e))
        }
    }

    protected open fun checkIfCanNotExtractTextForFile(file: File): ExtractionResult? {
        if (isAvailable == false) {
            return ExtractionResult(ErrorInfo(ErrorType.ExtractorNotAvailable))
        }

        if (canExtractDataFromFile(file) == false) {
            return ExtractionResult(ErrorInfo(ErrorType.FileTypeNotSupportedByExtractor))
        }

        return null // can extract text for file
    }

}