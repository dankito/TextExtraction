package net.dankito.text.extraction.model


enum class PdfType {

    /**
     * A PDF that only embeds images. No text can be select by mouse in such PDFs.
     *
     * -> We have to extract images from it and then apply OCR on them.
     */
    ImageOnlyPdf,

    /**
     * A PDF containing text. You can recognize such PDFs by selecting text in them or that
     * you can search for text.
     */
    SearchableTextPdf

}