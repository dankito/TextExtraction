package net.dankito.text.extraction.image

import net.dankito.text.extraction.ExternalToolTextExtractorBase
import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.model.ErrorInfo
import net.dankito.text.extraction.model.ErrorType
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Page
import net.dankito.utils.io.FileUtils
import net.dankito.utils.process.CommandConfig
import net.dankito.utils.process.CommandExecutor
import net.dankito.utils.process.ICommandExecutor
import java.io.File
import java.time.Duration
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit


open class FineReaderCommandlineImageTextExtractor @JvmOverloads constructor(
    commandExecutor: ICommandExecutor = CommandExecutor(),
    /**
     * Only needed for UI applications that like to show an hint to user when external application isn't found.
     */
    installHintLocalization: ResourceBundle = ResourceBundle.getBundle("Messages")
) : ExternalToolTextExtractorBase("FineCom", commandExecutor, installHintLocalization = installHintLocalization) {

    companion object {

        val MaxTimeToWaitForResult: Duration = Duration.ofMinutes(3)

        const val MillisToWaitBetweenChecks = 500L

        const val InvisibleCharacterAtStartOrEnd = '\uFEFF'

    }


    protected val outputFolder = FileUtils().createDirectoryInTempDir("FineReaderCommandlineTextExtractorOutput")


    override val name = "FineReader 11 or 12 Commandline"

    // TODO: set the ones FineReader 11 and 12 really support
    override val supportedFileTypes = listOf("png", "tif", "tiff", "jpg", "jpeg", "jpe", "gif",
        "jp2", "j2k", "jpf", "jpx", "jpc", "bmp", "dib", "rle", "dcx", "djvu", "djv", "jb2", "jbig2",
        "pcx", "wdp", "wmp", "hdp", "xps", "pdf")

    override val installHint = installHintLocalization.getString("error.message.finereader.commandline.not.found")

    override fun getTextExtractionQualityForFileType(file: File): Int {
        if (isFileTypeSupported(file)) {
            return 90
        }

        return ITextExtractor.TextExtractionQualityForUnsupportedFileType
    }


    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        // TODO: or use ".htm" as output type?
        val expectedOutputFile = File(outputFolder, file.nameWithoutExtension + ".txt")

        val commandConfig = createCommandConfig(file, expectedOutputFile)

        executeCommand(commandConfig)

        waitForRecognitionResult(expectedOutputFile)

        return determineExtractionResult(expectedOutputFile)
    }

    protected open fun waitForRecognitionResult(expectedOutputFile: File) {
        var outputFileFound = false
        val timeOut = LocalTime.now().plus(MaxTimeToWaitForResult)

        while (outputFileFound == false && LocalTime.now().isBefore(timeOut)) {
            try { TimeUnit.MILLISECONDS.sleep(MillisToWaitBetweenChecks) } catch (e: Exception) { }

            outputFileFound = expectedOutputFile.exists()
        }
    }

    protected open fun determineExtractionResult(expectedOutputFile: File): ExtractionResult {
        val extractedText =
            if (expectedOutputFile.exists()) expectedOutputFile.readText() // TODO: read non-blocking
            else null

        expectedOutputFile.delete()

        if (extractedText == null) {
            return ExtractionResult(ErrorInfo(ErrorType.ParseError))
        }

        val pageText = fixExtractedText(extractedText)

        return ExtractionResult(pages = listOf(Page(pageText, 1)))
    }

    protected open fun fixExtractedText(extractedText: String): String {
        val text = StringBuilder(extractedText.trim())

        while (text.startsWith(InvisibleCharacterAtStartOrEnd)) { // why does trim() not remove this character?
            text.deleteCharAt(0)
        }
        while (text.endsWith(InvisibleCharacterAtStartOrEnd)) { // why does trim() not remove this character?
            text.deleteCharAt(text.length - 1)
        }

        return text.toString().trim()
    }

    protected open fun createCommandConfig(fileToRecognize: File, expectedOutputFile: File): CommandConfig {
        val commandArgs = mutableListOf<String>()

        commandArgs.add(commandlineProgram.programExecutablePath)

        commandArgs.add(fileToRecognize.absolutePath) // TODO: surround with '"'?

        commandArgs.add("/lang")

        commandArgs.add("Mixed")

        commandArgs.add("/out")

        commandArgs.add(expectedOutputFile.absolutePath) // TODO: surround with '"'?

        commandArgs.add("/quit")

        return CommandConfig(commandArgs)
    }

}