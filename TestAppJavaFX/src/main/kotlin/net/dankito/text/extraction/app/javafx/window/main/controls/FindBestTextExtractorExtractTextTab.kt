package net.dankito.text.extraction.app.javafx.window.main.controls

import net.dankito.text.extraction.FindBestTextExtractor
import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.ITextExtractorRegistry
import net.dankito.utils.IThreadPool


class FindBestTextExtractorExtractTextTab(
    protected val extractorRegistry: ITextExtractorRegistry,
    threadPool: IThreadPool
) : ExtractTextTabBase(threadPool) {

    override fun createTextExtractor(): ITextExtractor {
        return FindBestTextExtractor(extractorRegistry)
    }

}