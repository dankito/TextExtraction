package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ExternalToolTextExtractorBase
import net.dankito.text.extraction.ITextExtractor.Companion.TextExtractionQualityForUnsupportedFileType
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Metadata
import net.dankito.text.extraction.model.Page
import net.dankito.utils.process.CommandConfig
import net.dankito.utils.process.CommandExecutor
import net.dankito.utils.process.ExecuteCommandResult
import net.dankito.utils.process.ICommandExecutor
import org.slf4j.LoggerFactory
import java.io.File


open class pdfToTextPdfTextExtractor @JvmOverloads constructor(
    commandExecutor: ICommandExecutor = CommandExecutor()
) : ExternalToolTextExtractorBase("pdftotext", commandExecutor), ISearchablePdfTextExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(pdfToTextPdfTextExtractor::class.java)
    }


    override val name = "pdftotext"

    override val supportedFileTypes = listOf("pdf")

    override fun getTextExtractionQualityForFileType(file: File): Int {
        if (isFileTypeSupported(file)) {
            return 99
        }

        return TextExtractionQualityForUnsupportedFileType
    }


    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        val result = ExtractionResult(metadata = getPdfMetadata(file))

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
        val result = ExtractionResult(null, "application/pdf", getPdfMetadata(file))

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


    protected open fun getPdfMetadata(file: File): Metadata? {
        try {
            val metadataResult = executeCommandWithLittleOutput(
                "pdfinfo",
                file.absolutePath
            )

            val title = getMetadataField(metadataResult, "Title")
            val author = getMetadataField(metadataResult, "Author")
            val length = getMetadataField(metadataResult, "Pages")?.toInt()
            val keywords = getMetadataField(metadataResult, "Keywords")

            return Metadata(title, author, length, keywords = keywords)
        } catch (e: Exception) {
            log.error("Could not get PDF metadata of file $file", e)
        }

        return null
    }

    protected open fun getMetadataField(metadataResult: ExecuteCommandResult, fieldName: String): String? {
        return metadataResult.outputLines
            .firstOrNull { it.startsWith(fieldName) }
            ?.replace("$fieldName:", "")
            ?.trim()
    }

}