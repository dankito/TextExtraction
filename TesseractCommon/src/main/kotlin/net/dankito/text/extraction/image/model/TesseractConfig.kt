package net.dankito.text.extraction.image.model

import java.io.File


open class TesseractConfig @JvmOverloads constructor(
    val ocrLanguages: List<OcrLanguage> = listOf(OcrLanguage.English),
    val ocrOutputType: OcrOutputType = OcrOutputType.Text,
    val tesseractPath: File? = File("tessdata"),
    val tessdataDirectory: File? = null,
    val pageSegMode: PageSegMode? = null
) {

    override fun toString(): String {
        return "tesseractPath=$tesseractPath, tessdataDirectory=$tessdataDirectory, ocrLanguages=$ocrLanguages, ocrOutputType=$ocrOutputType, pageSegMode=$pageSegMode"
    }

}