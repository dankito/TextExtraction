package net.dankito.text.extraction.app.javafx.window.main.controls

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.image.Tesseract4ImageTextExtractor
import net.dankito.text.extraction.image.model.OcrLanguage
import net.dankito.text.extraction.image.model.TesseractConfig
import net.dankito.utils.IThreadPool
import java.io.File


class Tesseract4ImageTextExtractorExtractTextTab(threadPool: IThreadPool) : ExtractTextTabBase(threadPool) {

    override fun createTextExtractor(): ITextExtractor {
        return Tesseract4ImageTextExtractor(TesseractConfig(listOf(OcrLanguage.English, OcrLanguage.German)))
    }

}