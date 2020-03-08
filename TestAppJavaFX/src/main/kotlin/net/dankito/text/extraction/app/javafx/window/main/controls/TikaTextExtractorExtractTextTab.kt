package net.dankito.text.extraction.app.javafx.window.main.controls

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.TikaTextExtractor


class TikaTextExtractorExtractTextTab : ExtractTextTabBase() {

    override fun createTextExtractor(): ITextExtractor {
        return TikaTextExtractor()
    }

}