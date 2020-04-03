package net.dankito.text.extraction.image

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.image.model.OcrLanguage
import net.dankito.text.extraction.image.model.TesseractConfig


class Tesseract4JniImageTextExtractorTest : ImageTextExtractorTestBase() {

    override fun createExtractorForLanguage(language: OcrLanguage): ITextExtractor {
        return Tesseract4JniImageTextExtractor(TesseractConfig(listOf(language)))
    }

}