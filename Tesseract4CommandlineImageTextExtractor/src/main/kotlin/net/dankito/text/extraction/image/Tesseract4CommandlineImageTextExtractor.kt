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
import net.dankito.utils.process.CommandExecutor
import net.dankito.utils.process.ExecuteCommandResult
import net.dankito.utils.process.ICommandExecutor
import java.io.File


open class Tesseract4CommandlineImageTextExtractor @JvmOverloads constructor(
    protected val config: TesseractConfig,
    protected val tesseractHelper: TesseractHelper = TesseractHelper(),
    commandExecutor: ICommandExecutor = CommandExecutor()
) : ExternalToolTextExtractorBase("tesseract", commandExecutor), IImageTextExtractor {


    override val name = "Tesseract 4"

    override val supportedFileTypes = TesseractHelper.SupportedFileTypes

    override fun getTextExtractionQualityForFileType(file: File): Int {
        if (isFileTypeSupported(file)) {
            return 70
        }

        return TextExtractionQualityForUnsupportedFileType
    }



    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        val commandConfig = createCommandConfig(file)

        val executeCommandResult = executeCommand(commandConfig)

        return mapExecuteCommandResult(executeCommandResult)
    }

    override suspend fun extractTextForSupportedFormatSuspendable(file: File): ExtractionResult {
        val commandConfig = createCommandConfig(file)

        val executeCommandResult = executeCommandSuspendable(commandConfig)

        return mapExecuteCommandResult(executeCommandResult)
    }

    protected open fun createCommandConfig(file: File): CommandConfig {
        val commandArgs = mutableListOf<String>()

        commandArgs.add(config.tesseractPath?.absolutePath ?: commandlineProgram.programExecutablePath)

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

        val environmentVariables = mutableMapOf<String, String>()

        if (config.willMultipleTesseractInstancesRunInParallel) {
            // Tesseract 4 uses 4 threads by default; when multiple Tesseract process are run in parallel
            // these block each other so that command never returns. To fix this limit count threads to 1
            environmentVariables["OMP_THREAD_LIMIT"] = "1"
        }

        return CommandConfig(commandArgs, null, environmentVariables)
    }

    protected open fun mapExecuteCommandResult(executeCommandResult: ExecuteCommandResult): ExtractionResult {
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