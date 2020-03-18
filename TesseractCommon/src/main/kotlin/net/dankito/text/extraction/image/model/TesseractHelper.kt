package net.dankito.text.extraction.image.model


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

    fun getTesseractOptionName(ocrOutputType: OcrOutputType): String {
        return when (ocrOutputType) {
            OcrOutputType.Hocr -> "hocr"
            OcrOutputType.Text -> "text"
        }
    }

}