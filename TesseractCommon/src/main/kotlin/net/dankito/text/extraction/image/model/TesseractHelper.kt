package net.dankito.text.extraction.image.model

import java.io.File


open class TesseractHelper {

    companion object {
        val SupportedFileTypes = listOf("png", "jpg", "jpeg", "tif", "tiff", "gif", "webp") // are there additional supported image formats?
    }


    open fun getTesseractLanguageString(ocrLanguages: List<OcrLanguage>): String {
        return ocrLanguages.map { getTesseractLanguageName(it) }.joinToString("+")
    }

    open fun getTesseractLanguageName(ocrLanguage: OcrLanguage): String {
        return when (ocrLanguage) {
            OcrLanguage.English -> "eng"
            OcrLanguage.German -> "deu"
        }
    }

    open fun getTesseractOptionName(ocrOutputType: OcrOutputType): String {
        return when (ocrOutputType) {
            OcrOutputType.Hocr -> "hocr"
            OcrOutputType.Text -> "text"
        }
    }


    open fun isTesseractCompatibleImageFileType(file: File): Boolean {
        return SupportedFileTypes.contains(file.extension.toLowerCase())
    }

}