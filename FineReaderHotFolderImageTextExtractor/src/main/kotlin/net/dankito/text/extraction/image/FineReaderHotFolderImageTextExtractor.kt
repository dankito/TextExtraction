package net.dankito.text.extraction.image

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.dankito.text.extraction.ITextExtractor.Companion.TextExtractionQualityForUnsupportedFileType
import net.dankito.text.extraction.TextExtractorBase
import net.dankito.text.extraction.model.ErrorInfo
import net.dankito.text.extraction.model.ErrorType
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Page
import net.dankito.utils.extensions.htmlToPlainText
import java.io.File
import java.time.Duration
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit


open class FineReaderHotFolderImageTextExtractor(
    protected val config: FineReaderHotFolderConfig,
    /**
     * Only needed for UI applications that like to show an hint to user when external application isn't found.
     */
    installHintLocalization: ResourceBundle = ResourceBundle.getBundle("Messages")
) : TextExtractorBase() {

    companion object {

        val MaxTimeToWaitForResult: Duration = Duration.ofMinutes(3)

        const val MillisToWaitBetweenChecks = 500L

        const val InvisibleCharacterAtStartOrEnd = '\uFEFF'


        const val TestFileResourceName = "TestImage.png"

        const val TestFileText = "You must be the change you want to see in the world."

    }


    protected var isAvailableField = false


    override val name = "FineReader HotFolder"

    override val supportedFileTypes = listOf("png", "tif", "tiff", "jpg", "jpeg", "jpe", "gif",
        "jp2", "j2k", "jpf", "jpx", "jpc", "bmp", "dib", "rle", "dcx", "djvu", "djv", "jb2", "jbig2",
        "pcx", "wdp", "wmp", "hdp", "xps", "pdf")


    override var isIsAvailableDeterminedYet = false
        protected set

    override val isAvailable: Boolean
        get() = isIsAvailableDeterminedYet && isAvailableField

    override val installHint = String.format(installHintLocalization.getString("error.message.finereader.hotfolder.not.found") ?: "",
                                            config.hotFolderPath.absolutePath, config.outputFolder.absolutePath)

    override fun getTextExtractionQualityForFileType(file: File): Int {
        if (isFileTypeSupported(file)) {
            return 95
        }

        return TextExtractionQualityForUnsupportedFileType
    }


    init {
        detectIsAvailableAsync()
    }


    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        if (config.hotFolderPath.exists() == false || config.outputFolder.exists() == false) {
            return ExtractionResult(ErrorInfo(ErrorType.ExtractorNotAvailable))
        }
        
        val expectedPlainTextOutputFile = File(config.outputFolder, file.nameWithoutExtension + ".txt")
        val expectedHtmlOutputFile = File(config.outputFolder, file.nameWithoutExtension + ".htm")

        file.copyTo(File(config.hotFolderPath, file.name), true)

        waitForRecognitionResult(expectedPlainTextOutputFile, expectedHtmlOutputFile)

        return determineExtractionResult(expectedPlainTextOutputFile, expectedHtmlOutputFile)
    }

    protected open fun waitForRecognitionResult(expectedPlainTextOutputFile: File, expectedHtmlOutputFile: File) {
        var plainTextOutputFileFound = false
        var htmlOutputFileFound = false
        val timeOut = LocalTime.now().plus(MaxTimeToWaitForResult)

        while (plainTextOutputFileFound == false && htmlOutputFileFound == false && LocalTime.now().isBefore(timeOut)) {
            try { TimeUnit.MILLISECONDS.sleep(MillisToWaitBetweenChecks) } catch (e: Exception) { }

            plainTextOutputFileFound = expectedPlainTextOutputFile.exists()
            htmlOutputFileFound = expectedHtmlOutputFile.exists()
        }
    }

    // TODO: how to get rid of duplicated code?
    override suspend fun extractTextForSupportedFormatSuspendable(file: File): ExtractionResult {
        if (config.hotFolderPath.exists() == false || config.outputFolder.exists() == false) {
            return ExtractionResult(ErrorInfo(ErrorType.ExtractorNotAvailable))
        }

        val expectedPlainTextOutputFile = File(config.outputFolder, file.nameWithoutExtension + ".txt")
        val expectedHtmlOutputFile = File(config.outputFolder, file.nameWithoutExtension + ".htm")

        file.copyTo(File(config.hotFolderPath, file.name), true)

        waitForRecognitionResultSuspendable(expectedPlainTextOutputFile, expectedHtmlOutputFile)

        return determineExtractionResult(expectedPlainTextOutputFile, expectedHtmlOutputFile)
    }

    protected open suspend fun waitForRecognitionResultSuspendable(expectedPlainTextOutputFile: File, expectedHtmlOutputFile: File) {
        var plainTextOutputFileFound = false
        var htmlOutputFileFound = false
        val timeOut = LocalTime.now().plus(MaxTimeToWaitForResult)

        while (plainTextOutputFileFound == false && htmlOutputFileFound == false && LocalTime.now().isBefore(timeOut)) {
            try { delay(MillisToWaitBetweenChecks) } catch (e: Exception) { }

            plainTextOutputFileFound = expectedPlainTextOutputFile.exists()
            htmlOutputFileFound = expectedHtmlOutputFile.exists()
        }
    }

    protected open fun determineExtractionResult(expectedPlainTextOutputFile: File, expectedHtmlOutputFile: File): ExtractionResult {
        val extractedText =
            if (expectedPlainTextOutputFile.exists()) expectedPlainTextOutputFile.readText() // TODO: read non-blocking
            else if (expectedHtmlOutputFile.exists()) expectedHtmlOutputFile.readText().htmlToPlainText()
            else null

        expectedPlainTextOutputFile.delete()
        expectedHtmlOutputFile.delete()

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


    open fun updateConfigAndDetectIsAvailableAsync(hotFolderPath: File, hotFolderOutputPath: File) {
        config.hotFolderPath = hotFolderPath
        config.outputFolder = hotFolderOutputPath

        detectIsAvailableAsync()
    }

    open fun detectIsAvailableAsync() = GlobalScope.launch(Dispatchers.IO) {
        javaClass.classLoader.getResource(TestFileResourceName)?.let { fileUrl ->
            val testFile = File(fileUrl.toURI())

            val extractionResult = extractTextForSupportedFormatSuspendable(testFile)

            isAvailableField = TestFileText == extractionResult.text
            isIsAvailableDeterminedYet = true

            determinedIsAvailableDetermined(isAvailable)
        }

    }

}