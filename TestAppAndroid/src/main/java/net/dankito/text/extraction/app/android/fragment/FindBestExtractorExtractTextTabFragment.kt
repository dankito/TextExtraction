package net.dankito.text.extraction.app.android.fragment

import net.dankito.text.extraction.FindBestTextExtractor
import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.ITextExtractorRegistry


class FindBestExtractorExtractTextTabFragment(val extractorRegistry: ITextExtractorRegistry) : ExtractTextTabFragment() {

    override fun createTextExtractor(): ITextExtractor {
        return FindBestTextExtractor(extractorRegistry)
    }

}