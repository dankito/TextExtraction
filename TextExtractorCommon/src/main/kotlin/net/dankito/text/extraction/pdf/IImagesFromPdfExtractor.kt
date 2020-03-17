package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.model.ExtractedImages
import java.io.File


interface IImagesFromPdfExtractor {

    val isAvailable: Boolean


    fun extractImages(pdfFile: File): ExtractedImages

    suspend fun extractImagesSuspendable(pdfFile: File): ExtractedImages

}