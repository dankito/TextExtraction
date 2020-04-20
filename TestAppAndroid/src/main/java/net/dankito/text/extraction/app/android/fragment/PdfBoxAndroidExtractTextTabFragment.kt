package net.dankito.text.extraction.app.android.fragment

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.pdf.PdfBoxAndroidPdfTextExtractor


class PdfBoxAndroidExtractTextTabFragment : ExtractTextTabFragment() {

    override fun createTextExtractor(): ITextExtractor {
        return PdfBoxAndroidPdfTextExtractor(context?.applicationContext!!)
    }

}