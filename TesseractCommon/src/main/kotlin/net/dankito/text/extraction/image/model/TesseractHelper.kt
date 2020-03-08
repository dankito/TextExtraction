package net.dankito.text.extraction.image.model


open class TesseractHelper {

    open fun getTesseractLanguageString(ocrLanguages: List<OcrLanguage>): String {
        return ocrLanguages.map { getTesseractLanguageName(it) }.joinToString("+")
    }

    open fun getTesseractLanguageName(ocrLanguage: OcrLanguage): String {
        return when (ocrLanguage) {
            OcrLanguage.English -> "eng"
            OcrLanguage.German -> "deu"
        }
    }

}