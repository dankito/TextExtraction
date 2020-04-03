package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.TextExtractorTestBase
import net.dankito.text.extraction.TextExtractorType
import net.dankito.text.extraction.image.model.OcrLanguage


abstract class PdfTextExtractorTestBase : TextExtractorTestBase() {


    abstract fun createExtractor(): ITextExtractor

    override val textExtractorType = TextExtractorType.Pdf

    override fun createExtractorForLanguage(language: OcrLanguage): ITextExtractor {
        return createExtractor()
    }

}