package net.dankito.text.extraction.app.javafx.window.main.controls

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.TikaTextExtractor
import net.dankito.utils.IThreadPool


class TikaTextExtractorExtractTextTab(threadPool: IThreadPool) : ExtractTextTabBase(threadPool) {

    override fun createTextExtractor(): ITextExtractor {
        return TikaTextExtractor()
    }

}