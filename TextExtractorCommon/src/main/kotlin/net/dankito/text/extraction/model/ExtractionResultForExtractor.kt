package net.dankito.text.extraction.model

import net.dankito.text.extraction.ITextExtractor


open class ExtractionResultForExtractor(
    val extractor: ITextExtractor?,
    error: ErrorInfo? = null,
    contentType: String? = null,
    metadata: Metadata? = null
): ExtractionResult(error, contentType, metadata) {

    constructor(extractor: ITextExtractor?, extractionResult: ExtractionResult) :
            this(extractor, extractionResult.error, extractionResult.contentType, extractionResult.metadata)


    override fun toString(): String {
        return "ExtractionResult ${super.toString()} for $extractor"
    }

}