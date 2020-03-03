package net.dankito.text.extraction.model


open class TikaSettings(
	val pdfContentExtractorStrategy: PdfContentExtractorStrategy,
	val ocrLanguages: List<OcrLanguage> = listOf(OcrLanguage.English),
	val ocrOutputType: OcrOutputType = OcrOutputType.Text,
	val tesseractPath: String? = null,
	val tessdataPath: String? = null
) {

	override fun toString(): String {
		return "pdfContentExtractorStrategy=$pdfContentExtractorStrategy, ocrLanguages=$ocrLanguages, ocrOutputType=$ocrOutputType, tesseractPath=$tesseractPath, tessdataPath=$tessdataPath"
	}

}