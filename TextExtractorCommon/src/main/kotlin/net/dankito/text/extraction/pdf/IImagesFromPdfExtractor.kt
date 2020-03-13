package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.model.ExtractedImages
import java.io.File


interface IImagesFromPdfExtractor {

    fun extractImages(pdfFile: File): ExtractedImages

}