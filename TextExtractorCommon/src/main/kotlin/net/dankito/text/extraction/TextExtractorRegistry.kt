package net.dankito.text.extraction

import net.dankito.text.extraction.model.*
import net.dankito.text.extraction.pdf.IImageOnlyPdfTextExtractor
import net.dankito.text.extraction.pdf.IPdfTypeDetector
import net.dankito.text.extraction.pdf.ISearchablePdfTextExtractor
import java.io.File


open class TextExtractorRegistry @JvmOverloads constructor(
    protected val pdfTypeDetector: IPdfTypeDetector? = null,
    extractors: List<ITextExtractor> = listOf()
) : ITextExtractorRegistry {

    constructor(extractors: List<ITextExtractor> = listOf()) : this(null, extractors)


    protected val availableExtractors: MutableList<ITextExtractor> = ArrayList(extractors)

    override val extractors: List<ITextExtractor>
        get() = ArrayList(availableExtractors)


    override fun getAllExtractorsForFile(file: File): List<ITextExtractor> {
        val pdfType = determinePdfType(file)

        return extractors
            .sortedByDescending { it.getTextExtractionQualityForFileType(file) }
            .filter { canExtractDataFromFile(it, file, pdfType) }
    }

    override fun findBestExtractorForFile(file: File): ITextExtractor? {
        val pdfType = determinePdfType(file)

        return extractors
            .sortedByDescending { it.getTextExtractionQualityForFileType(file) }
            .firstOrNull { canExtractDataFromFile(it, file, pdfType) }
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


    protected open fun canExtractDataFromFile(extractor: ITextExtractor, file: File, pdfType: PdfType?): Boolean {
        return extractor.isAvailable && extractor.canExtractDataFromFile(file) && passesPdfTypeCheck(extractor, file, pdfType)
    }

    protected open fun determinePdfType(file: File): PdfType? {
        return if (file.extension.toLowerCase() == "pdf" && pdfTypeDetector?.isAvailable == true) {
            pdfTypeDetector.detectPdfType(file)
        }
        else {
            null
        }
    }

    protected open fun passesPdfTypeCheck(extractor: ITextExtractor, file: File, pdfType: PdfType?): Boolean {
        if (pdfType == null) {
            return true
        }

        if (pdfType == PdfType.ImageOnlyPdf) {
            return extractor is IImageOnlyPdfTextExtractor
        }
        else if (pdfType == PdfType.SearchableTextPdf) {
            return extractor is ISearchablePdfTextExtractor
        }

        return true // not a PDF file -> // TODO: check if extractor is not a PdfTextExtractor
    }


    open fun addExtractor(extractor: ITextExtractor): Boolean {
        return availableExtractors.add(extractor)
    }

    open fun removeExtractor(extractor: ITextExtractor): Boolean {
        return availableExtractors.remove(extractor)
    }

}