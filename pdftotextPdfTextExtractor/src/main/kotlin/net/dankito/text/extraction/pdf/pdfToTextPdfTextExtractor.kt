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
    commandExecutor: ICommandExecutor = CommandExecutor(),
    protected val metadataExtractor: IPdfMetadataExtractor = pdfinfoPdfMetadataExtractor(commandExecutor)
) : ExternalToolTextExtractorBase("pdftotext", commandExecutor), ISearchablePdfTextExtractor {


    override val name = "pdftotext"

    override val supportedFileTypes = listOf("pdf")

    override fun getTextExtractionQualityForFileType(file: File): Int {
        if (isFileTypeSupported(file)) {
            return 99
        }

        return TextExtractionQualityForUnsupportedFileType
    }


    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        val metadata = metadataExtractor.extractMetadata(file)
        val result = ExtractionResult(null, "application/pdf", metadata)
        val extractedLength = metadata?.length ?: 0

        if (extractedLength > 0) {
            for (pageNum in 1..extractedLength) {
                extractPage(file, result, pageNum)
            }
        }
        else {
            generateSequence(1) { it + 1 }.forEach { pageNum ->
                if (extractPage(file, result, pageNum) == false) { // if pageNum is out of range exitCode 99 gets returned and error message is 'Command Line Error: Wrong page range given: the first page (<count pages>) can not be after the last page (<count pages + 1>).'
                    return result
                }
            }
        }

        return result // should never come to this
    }

    protected open fun extractPage(file: File, result: ExtractionResult, pageNum: Int): Boolean {
        val pageResult = executeCommand(createCommandConfig(file, pageNum))

        if (pageResult.successful) {
            result.addPage(Page(pageResult.output, pageNum))
        }

        return pageResult.successful
    }

    // TODO: how to get rid of duplicated code?
    override suspend fun extractTextForSupportedFormatSuspendable(file: File): ExtractionResult {
        val metadata = metadataExtractor.extractMetadata(file)
        val result = ExtractionResult(null, "application/pdf", metadata)
        val extractedLength = metadata?.length ?: 0

        if (extractedLength > 0) {
            for (pageNum in 1..extractedLength) {
                extractPage(file, result, pageNum)
            }
        }
        else {
            generateSequence(1) { it + 1 }.forEach { pageNum ->
                if (extractPageSuspendable(file, result, pageNum) == false) { // if pageNum is out of range exitCode 99 gets returned and error message is 'Command Line Error: Wrong page range given: the first page (<count pages>) can not be after the last page (<count pages + 1>).'
                    return result
                }
            }
        }

        return result // should never come to this
    }

    protected open suspend fun extractPageSuspendable(file: File, result: ExtractionResult, pageNum: Int): Boolean {
        val pageResult = executeCommandSuspendable(createCommandConfig(file, pageNum))

        if (pageResult.successful) {
            result.addPage(Page(pageResult.output, pageNum))
        }

        return pageResult.successful
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
            commandlineProgram.programExecutablePath,
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