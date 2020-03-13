package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ITextExtractor


/**
 * Marker interface for [ITextExtractor] that extract text from searchable PDFs (that is PDFs
 * that consist of selectable text not just embedded images. The latter do [IImageBasedPdfTextExtractor]).
 */
interface ISearchablePdfTextExtractor : ITextExtractor