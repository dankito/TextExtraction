package net.dankito.text.extraction.image.model

import java.io.File


open class TesseractConfig @JvmOverloads constructor(
    val ocrLanguages: List<OcrLanguage> = listOf(OcrLanguage.English),
    val ocrOutputType: OcrOutputType = OcrOutputType.Text,
    val tesseractPath: File? = null,
    val tessdataDirectory: File? = null,
    val pageSegMode: PageSegMode? = null,
    /**
     * Tesseract by default uses 4 threads when recognizing an image. If multiple Tesseract instances
     * run in parallel, this will lead to that threads block each other and Tesseract never completes
     */
    val willMultipleTesseractInstancesRunInParallel: Boolean = false
) {

    override fun toString(): String {
        return "tesseractPath=$tesseractPath, tessdataDirectory=$tessdataDirectory, ocrLanguages=$ocrLanguages, ocrOutputType=$ocrOutputType, pageSegMode=$pageSegMode"
    }

}