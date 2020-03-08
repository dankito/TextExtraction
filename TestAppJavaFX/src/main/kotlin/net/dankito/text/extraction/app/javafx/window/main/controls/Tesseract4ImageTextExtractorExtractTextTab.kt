package net.dankito.text.extraction.app.javafx.window.main.controls

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.image.Tesseract4ImageTextExtractor
import net.dankito.text.extraction.image.model.OcrLanguage
import net.dankito.text.extraction.image.model.TesseractConfig


class Tesseract4ImageTextExtractorExtractTextTab : ExtractTextTabBase() {

    override fun createTextExtractor(): ITextExtractor {
        return Tesseract4ImageTextExtractor(TesseractConfig(listOf(OcrLanguage.English, OcrLanguage.German)))
    }

}