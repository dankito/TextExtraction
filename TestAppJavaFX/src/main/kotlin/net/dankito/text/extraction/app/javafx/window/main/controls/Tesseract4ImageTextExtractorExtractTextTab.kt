package net.dankito.text.extraction.app.javafx.window.main.controls

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.image.Tesseract4ImageTextExtractor
import net.dankito.text.extraction.image.model.Tesseract4Config
import net.dankito.utils.IThreadPool


class Tesseract4ImageTextExtractorExtractTextTab(threadPool: IThreadPool) : ExtractTextTabBase(threadPool) {

    override fun createTextExtractor(): ITextExtractor {
        return Tesseract4ImageTextExtractor(Tesseract4Config(languages = listOf("eng", "deu")))
    }

}