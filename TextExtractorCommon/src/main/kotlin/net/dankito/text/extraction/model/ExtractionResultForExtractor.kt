package net.dankito.text.extraction.model

import net.dankito.text.extraction.ITextExtractor


open class ExtractionResultForExtractor(
    val extractor: ITextExtractor?,
    error: ErrorInfo? = null,
    mimeType: String? = null,
    metadata: Metadata? = null
): ExtractionResult(error, mimeType, metadata) {

    constructor(extractor: ITextExtractor?, extractionResult: ExtractionResult) :
            this(extractor, extractionResult.error, extractionResult.mimeType, extractionResult.metadata)


    override fun toString(): String {
        return "ExtractionResult ${super.toString()} for $extractor"
    }

}