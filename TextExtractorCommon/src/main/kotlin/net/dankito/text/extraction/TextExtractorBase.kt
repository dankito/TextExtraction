package net.dankito.text.extraction

import net.dankito.text.extraction.model.ErrorInfo
import net.dankito.text.extraction.model.ErrorType
import net.dankito.text.extraction.model.ExtractionResult
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.CopyOnWriteArraySet


abstract class TextExtractorBase : ITextExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(TextExtractorBase::class.java)
    }


    protected val isIsAvailableDeterminedYetListeners = CopyOnWriteArraySet<(isAvailable: Boolean) -> Unit>()


    override val isIsAvailableDeterminedYet = true

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


    override fun addIsIsAvailableDeterminedYetListener(listener: (isAvailable: Boolean) -> Unit) {
        if (isIsAvailableDeterminedYet) {
            listener(isAvailable)
        }
        else {
            isIsAvailableDeterminedYetListeners.add(listener)
        }
    }

    protected fun determinedIsAvailableDetermined(isAvailable: Boolean) {
        val listeners = ArrayList(isIsAvailableDeterminedYetListeners)
        isIsAvailableDeterminedYetListeners.clear()

        listeners.forEach { listener ->
            listener(isAvailable)
        }
    }

}