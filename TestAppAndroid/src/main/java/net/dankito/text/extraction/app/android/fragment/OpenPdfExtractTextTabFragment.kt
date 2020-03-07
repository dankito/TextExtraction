package net.dankito.text.extraction.app.android.fragment

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.pdf.OpenPdfPdfTextExtractor


class OpenPdfExtractTextTabFragment : ExtractTextTabFragment() {

    override fun createTextExtractor(): ITextExtractor {
        return OpenPdfPdfTextExtractor()
    }

}