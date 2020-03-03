package net.dankito.text.extraction.model


enum class PdfContentExtractorStrategy {

	NoOcr,

	OcrOnly,

	OcrAndText;


	val isOcrEnabled: Boolean
		get() = this != NoOcr

}