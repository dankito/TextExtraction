package net.dankito.text.extraction.app.android.fragment

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.pdf.iText2PdfTextExtractor


class iText2ExtractTextTabFragment : ExtractTextTabFragment() {

    override fun createTextExtractor(): ITextExtractor {
        return iText2PdfTextExtractor()
    }

}