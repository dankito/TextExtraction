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
import net.dankito.utils.process.CommandConfig
import java.io.File


open class Tesseract4CommandlineImageTextExtractor @JvmOverloads constructor(
    protected val config: TesseractConfig,
    protected val tesseractHelper: TesseractHelper = TesseractHelper()
) : ExternalToolTextExtractorBase(), IImageTextExtractor {


    override val name = "Tesseract 4"

    override val isAvailable: Boolean =
        File(executeCommandWithLittleOutput("tesseract").output).exists() // TODO

    override val supportedFileTypes = TesseractHelper.SupportedFileTypes

    override fun getTextExtractionQualityForFileType(file: File): Int {
        if (isFileTypeSupported(file)) {
            return 50
        }

        return TextExtractionQualityForUnsupportedFileType
    }



    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        val commandArgs = mutableListOf<String>()

        commandArgs.add(config.tesseractPath?.absolutePath ?: "tesseract")

        commandArgs.add(file.absolutePath)

        commandArgs.add("stdout")

        config.tessdataDirectory?.let { tessdataDir ->
            commandArgs.add("--tessdata-dir")
            commandArgs.add(tessdataDir.absolutePath)
        }

        if (config.ocrLanguages.isNotEmpty()) {
            commandArgs.add("-l")
            commandArgs.add(tesseractHelper.getTesseractLanguageString(config.ocrLanguages))
        }

        config.pageSegMode?.let { pageSegmentMode ->
            commandArgs.add("--psm ")
            commandArgs.add(pageSegmentMode.toString())
        }

        if (config.ocrOutputType != OcrOutputType.Text) {
            commandArgs.add(tesseractHelper.getTesseractOptionName(config.ocrOutputType))
        }

        // Tesseract 4 uses 4 threads by default; when multiple Tesseract process are run in parallel
        // these block each other so that command never returns. To fix this limit count threads to 1
        val environmentVariables = mapOf("OMP_THREAD_LIMIT" to "1")

        val executeCommandResult = executeCommand(CommandConfig(commandArgs, null, environmentVariables))

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