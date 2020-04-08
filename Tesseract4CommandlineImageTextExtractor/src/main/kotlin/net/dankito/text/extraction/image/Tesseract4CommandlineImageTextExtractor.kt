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
import net.dankito.utils.process.*
import java.io.File
import java.util.*


open class Tesseract4CommandlineImageTextExtractor @JvmOverloads constructor(
    protected val config: TesseractConfig,
    protected val tesseractHelper: TesseractHelper = TesseractHelper(),
    commandExecutor: ICommandExecutor = CommandExecutor(),
    maxCountParallelExecutions: Int = CpuInfo.CountCores - 2,
    /**
     * Only needed for UI applications that like to show an hint to user when external application isn't found.
     */
    installHintLocalization: ResourceBundle = ResourceBundle.getBundle("Messages")
) : ExternalToolTextExtractorBase("tesseract", commandExecutor, maxCountParallelExecutions, installHintLocalization), IImageTextExtractor {


    override val name = "Tesseract 4"

    override val supportedFileTypes = TesseractHelper.SupportedFileTypes

    override val installHint = getInstallHintForOsType("error.message.tesseract.not.found.")

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

        commandArgs.add("stdout") // if 'stdout' is being removed from parameters list, detection if a Tesseract instance is already running will not work anymore

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

        if (config.willMultipleTesseractInstancesRunInParallel || isAnotherTesseractInstanceRunning()) {
            // Tesseract 4 uses 4 threads by default; when multiple Tesseract process are run in parallel
            // these block each other so that command never returns. To fix this limit count threads to 1
            environmentVariables["OMP_THREAD_LIMIT"] = "1"
        }

        return CommandConfig(commandArgs, null, environmentVariables)
    }

    protected open fun isAnotherTesseractInstanceRunning(): Boolean {
        val executableName = File(programExecutablePath).name

        val commandArgs = if (osHelper.isRunningOnWindows) {
            listOf("tasklist.exe")
        }
        else {
            // "To execute a pipeline, you have to invoke a shell, and then run your commands inside that shell", see https://stackoverflow.com/a/3776277
            listOf("/bin/sh", "-c", "ps aux | grep stdout")
        }

        val executionResult = commandExecutor.executeCommand(CommandConfig(commandArgs))

        return executionResult.outputLines.firstOrNull { it.contains(executableName, true) } != null
    }

    protected open fun mapExecuteCommandResult(executeCommandResult: ExecuteCommandResult): ExtractionResult {
        return if (executeCommandResult.successful) {
            val result = ExtractionResult() // TODO: set mime type
            result.addPage(Page(executeCommandResult.output))

            result
        }
        else {
            ExtractionResult(ErrorInfo(ErrorType.ParseError, Exception(executeCommandResult.errors)))
        }
    }

}