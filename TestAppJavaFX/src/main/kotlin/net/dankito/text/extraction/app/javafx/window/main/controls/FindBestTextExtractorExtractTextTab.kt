package net.dankito.text.extraction.app.javafx.window.main.controls

import net.dankito.text.extraction.FindBestTextExtractor
import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.ITextExtractorRegistry


class FindBestTextExtractorExtractTextTab(protected val extractorRegistry: ITextExtractorRegistry) : ExtractTextTabBase() {

    override fun createTextExtractor(): ITextExtractor {
        return FindBestTextExtractor(extractorRegistry)
    }

}