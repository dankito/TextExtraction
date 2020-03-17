package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ExternalToolTextExtractorBase
import net.dankito.text.extraction.ITextExtractor.Companion.TextExtractionQualityForUnsupportedFileType
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Page
import net.dankito.utils.process.CommandConfig
import net.dankito.utils.process.CommandExecutor
import net.dankito.utils.process.ICommandExecutor
import java.io.File


open class pdfToTextPdfTextExtractor @JvmOverloads constructor(
    protected val pdftotextExecutablePath: String = "pdftotext",
    commandExecutor: ICommandExecutor = CommandExecutor()
) : ExternalToolTextExtractorBase(commandExecutor), ISearchablePdfTextExtractor {


    override val name = "pdftotext"

    // TODO: adjust for a) Windows b) if pdftotextExecutablePath is set
    protected val didFindPdftotextExecutable: Boolean =
        File(executeCommandWithLittleOutput(pdftotextExecutablePath).output).exists()

    override val isAvailable = didFindPdftotextExecutable

    override val supportedFileTypes = listOf("pdf")

    override fun getTextExtractionQualityForFileType(file: File): Int {
        if (isFileTypeSupported(file)) {
            return 99
        }

        return TextExtractionQualityForUnsupportedFileType
    }


    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        val result = ExtractionResult()

        // to extract all text at once:
        // result.addPage(Page(executeCommand(pdftotextExecutablePath, "-layout", file.absolutePath, "-").output))

        generateSequence(1) { it + 1 }.forEach { pageNum ->
            val pageResult = executeCommand(createCommandConfig(file, pageNum))

            if (pageResult.successful) {
                result.addPage(Page(pageResult.output, pageNum))
            }
            else { // if pageNum is out of range exitCode 99 gets returned and error message is 'Command Line Error: Wrong page range given: the first page (<count pages>) can not be after the last page (<count pages + 1>).'
                return result
            }
        }

        return result // should never come to this
    }

    // TODO: how to get rid of duplicated code?
    override suspend fun extractTextForSupportedFormatSuspendable(file: File): ExtractionResult {
        val result = ExtractionResult()

        // to extract all text at once:
        // result.addPage(Page(executeCommand(pdftotextExecutablePath, "-layout", file.absolutePath, "-").output))

        generateSequence(1) { it + 1 }.forEach { pageNum ->
            val pageResult = executeCommandSuspendable(createCommandConfig(file, pageNum))

            if (pageResult.successful) {
                result.addPage(Page(pageResult.output, pageNum))
            }
            else { // if pageNum is out of range exitCode 99 gets returned and error message is 'Command Line Error: Wrong page range given: the first page (<count pages>) can not be after the last page (<count pages + 1>).'
                return result
            }
        }

        return result // should never come to this
    }

    protected open fun createCommandConfig(file: File, pageNum: Int): CommandConfig {
        /**
         * pdftotext command line arguments:
         *  -f <int>: first page to convert
         *  -l <int>: last page to convert
         *  -layout: maintain original physical layout
         *  - (last parameter): print to console instead of to file
         */
        // TODO: add .exe to pdftotext / pdftotextExecutablePath on Windows
        return CommandConfig(listOf(
            pdftotextExecutablePath,
            "-f",
            pageNum.toString(),
            "-l",
            pageNum.toString(),
            "-layout",
            file.absolutePath,
            "-"
        ))
    }

}