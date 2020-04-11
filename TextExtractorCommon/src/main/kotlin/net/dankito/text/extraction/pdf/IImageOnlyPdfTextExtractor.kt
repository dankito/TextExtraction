package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ITextExtractor


/**
 * Marker interface for [ITextExtractor] that extract text from 'image only PDFs', that are PDFs
 * that only contain images but no searchable text.
 * (Searchable text from PDFs can be extracted with [ISearchablePdfTextExtractor].)
 */
interface IImageOnlyPdfTextExtractor : ITextExtractor