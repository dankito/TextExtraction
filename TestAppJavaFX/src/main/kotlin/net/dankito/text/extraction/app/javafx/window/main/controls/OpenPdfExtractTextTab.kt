package net.dankito.text.extraction.app.javafx.window.main.controls

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.pdf.OpenPdfPdfTextExtractor
import net.dankito.utils.IThreadPool


class OpenPdfExtractTextTab(threadPool: IThreadPool) : ExtractTextTabBase(threadPool) {

    override fun createTextExtractor(): ITextExtractor {
        return OpenPdfPdfTextExtractor()
    }

}