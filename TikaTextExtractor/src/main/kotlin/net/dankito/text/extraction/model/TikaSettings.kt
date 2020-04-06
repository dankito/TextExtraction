package net.dankito.text.extraction.model

import net.dankito.text.extraction.image.model.OcrLanguage
import net.dankito.text.extraction.image.model.OcrOutputType
import net.dankito.text.extraction.image.model.PageSegMode
import net.dankito.text.extraction.image.model.TesseractConfig
import java.io.File


open class TikaSettings @JvmOverloads constructor(
	val enableOcrForImages: Boolean = true,
	val pdfContentExtractorStrategy: PdfContentExtractorStrategy = PdfContentExtractorStrategy.OcrAndText,
	ocrLanguages: List<OcrLanguage> = listOf(OcrLanguage.English),
	ocrOutputType: OcrOutputType = OcrOutputType.Text,
	tesseractPath: File? = null,
	tessdataDirectory: File? = null,
	pageSegMode: PageSegMode? = null
) : TesseractConfig(ocrLanguages, ocrOutputType, tesseractPath, tessdataDirectory, pageSegMode) {

	constructor(enableOcrForImages: Boolean, pdfContentExtractorStrategy: PdfContentExtractorStrategy, tesseractConfig: TesseractConfig)
		: this(enableOcrForImages, pdfContentExtractorStrategy, tesseractConfig.ocrLanguages, tesseractConfig.ocrOutputType,
			tesseractConfig.tesseractPath, tesseractConfig.tessdataDirectory, tesseractConfig.pageSegMode)


	override fun toString(): String {
		return "pdfContentExtractorStrategy=$pdfContentExtractorStrategy, ${super.toString()}"
	}

}