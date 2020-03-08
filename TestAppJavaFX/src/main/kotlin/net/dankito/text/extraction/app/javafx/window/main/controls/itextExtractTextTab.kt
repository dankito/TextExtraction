package net.dankito.text.extraction.app.javafx.window.main.controls

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.pdf.itextPdfTextExtractor


class itextExtractTextTab : ExtractTextTabBase() {

    override fun createTextExtractor(): ITextExtractor {
        return itextPdfTextExtractor()
    }

}