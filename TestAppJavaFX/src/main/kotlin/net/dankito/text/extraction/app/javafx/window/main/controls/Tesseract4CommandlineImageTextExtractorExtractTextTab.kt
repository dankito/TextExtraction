package net.dankito.text.extraction.app.javafx.window.main.controls

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.image.Tesseract4CommandlineImageTextExtractor
import net.dankito.text.extraction.image.model.OcrLanguage
import net.dankito.text.extraction.image.model.TesseractConfig


class Tesseract4CommandlineImageTextExtractorExtractTextTab : ExtractTextTabBase() {

    override fun createTextExtractor(): ITextExtractor {
        return Tesseract4CommandlineImageTextExtractor(TesseractConfig(listOf(OcrLanguage.English, OcrLanguage.German)))
    }

}