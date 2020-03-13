package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ITextExtractor


/**
 * Marker interface for [ITextExtractor] that extract text from images embedded in PDFs (that is
 * not their searchable text parts, that do [ISearchablePdfTextExtractor]).
 */
interface IImageBasedPdfTextExtractor : ITextExtractor