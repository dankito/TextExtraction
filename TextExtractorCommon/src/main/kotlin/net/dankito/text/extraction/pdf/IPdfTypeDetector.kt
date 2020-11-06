package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.model.PdfType
import java.io.File


interface IPdfTypeDetector {

    val isAvailable: Boolean


    fun detectPdfType(file: File): PdfType?

}