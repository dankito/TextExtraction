package net.dankito.text.extraction.model

import net.dankito.text.extraction.ITextExtractor


open class ExtractionResultForExtractor(
    val extractor: ITextExtractor?,
    error: ErrorInfo? = null,
    contentType: String? = null,
    metadata: Metadata? = null,
    pages: List<Page> = listOf()
): ExtractionResult(error, contentType, metadata, pages) {

    constructor(extractor: ITextExtractor?, extractionResult: ExtractionResult) :
            this(extractor, extractionResult.error, extractionResult.contentType, extractionResult.metadata, extractionResult.pages)


    override fun toString(): String {
        return "ExtractionResult ${super.toString()} for $extractor"
    }

}