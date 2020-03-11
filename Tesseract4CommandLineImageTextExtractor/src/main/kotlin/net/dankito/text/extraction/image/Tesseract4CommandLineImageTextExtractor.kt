package net.dankito.text.extraction.image

import net.dankito.text.extraction.ExternalToolTextExtractorBase
import net.dankito.text.extraction.ITextExtractor.Companion.TextExtractionQualityForUnsupportedFileType
import net.dankito.text.extraction.image.model.OcrOutputType
import net.dankito.text.extraction.image.model.TesseractConfig
import net.dankito.text.extraction.image.model.TesseractHelper
import net.dankito.text.extraction.model.ErrorInfo
import net.dankito.text.extraction.model.ErrorType
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Page
import java.io.File


open class Tesseract4CommandLineImageTextExtractor(
    protected val config: TesseractConfig,
    protected val tesseractHelper: TesseractHelper = TesseractHelper()
) : ExternalToolTextExtractorBase() {


    override val name = "Tesseract 4"

    override val isAvailable: Boolean =
        File(executeCommand("which", "tesseract").output).exists() // TODO

    override val supportedFileTypes = TesseractHelper.SupportedFileTypes

    override fun getTextExtractionQualityForFileType(file: File): Int {
        if (isFileTypeSupported(file)) {
            return 50
        }

        return TextExtractionQualityForUnsupportedFileType
    }



    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        val command = StringBuilder()

        command.append(config.tesseractPath?.absolutePath ?: "tesseract")

        command.append(" " + file.absolutePath)

        command.append(" stdout ")

        config.tessdataDirectory?.let { tessdataDir ->
            command.append("--tessdata-dir ${tessdataDir.absolutePath} ")
        }

        if (config.ocrLanguages.isNotEmpty()) {
            command.append("-l ${tesseractHelper.getTesseractLanguageString(config.ocrLanguages)} ")
        }

        config.pageSegMode?.let { pageSegmentMode ->
            command.append("--psm $pageSegmentMode ")
        }

        if (config.ocrOutputType != OcrOutputType.Text) {
            command.append(tesseractHelper.getTesseractOptionName(config.ocrOutputType))
        }

        val executeCommandResult = executeCommand(command.toString())

        return if (executeCommandResult.successful) {
            val result = ExtractionResult()
            result.addPage(Page(executeCommandResult.output))

            result
        }
        else {
            ExtractionResult(ErrorInfo(ErrorType.ParseError, Exception(executeCommandResult.errors)))
        }
    }

}