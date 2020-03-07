package net.dankito.text.extraction.model

import net.dankito.text.extraction.ITextExtractor


open class ExtractionResultForExtractor(val extractor: ITextExtractor?, errorInfo: ErrorInfo?): ExtractionResult(errorInfo) {

    constructor(extractor: ITextExtractor, extractionResult: ExtractionResult) : this(extractor, null) {
        this.pagesField.addAll(extractionResult.pages)
    }


    override fun toString(): String {
        return "ExtractionResult ${super.toString()} for $extractor"
    }

}