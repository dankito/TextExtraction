package net.dankito.text.extraction.app.javafx.window.main.controls

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.pdf.OpenPdfPdfTextExtractor


class OpenPdfExtractTextTab : ExtractTextTabBase() {

    override fun createTextExtractor(): ITextExtractor {
        return OpenPdfPdfTextExtractor()
    }

}