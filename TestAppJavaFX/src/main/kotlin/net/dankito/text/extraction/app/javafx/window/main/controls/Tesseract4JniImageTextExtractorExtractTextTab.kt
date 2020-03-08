package net.dankito.text.extraction.app.javafx.window.main.controls

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.image.Tesseract4JniImageTextExtractor
import net.dankito.text.extraction.image.model.OcrLanguage
import net.dankito.text.extraction.image.model.TesseractConfig


class Tesseract4JniImageTextExtractorExtractTextTab : ExtractTextTabBase() {

    override fun createTextExtractor(): ITextExtractor {
        return Tesseract4JniImageTextExtractor(TesseractConfig(listOf(OcrLanguage.English, OcrLanguage.German)))
    }

}