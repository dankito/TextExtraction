package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.model.ErrorInfo
import net.dankito.text.extraction.model.ErrorType
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Page
import org.slf4j.LoggerFactory
import java.io.File


open class pdfToTextPdfTextExtractor @JvmOverloads constructor(protected val pdftotextExecutablePath: String = "pdftotext") : ITextExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(pdfToTextPdfTextExtractor::class.java)
    }


    // TODO: adjust for a) Windows b) if pdftotextExecutablePath is set
    protected val didFindPdftotextExecutable: Boolean =
        File(executeCommand("which", pdftotextExecutablePath).output).exists()

    override val isAvailable = didFindPdftotextExecutable

    override val textExtractionQuality = 95

    override fun canExtractDataFromFile(file: File): Boolean {
        return "pdf" == file.extension.toLowerCase()
    }


    override fun extractText(file: File): ExtractionResult {
        if (isAvailable) {
            // to extract all text at once:
            // result.addPage(Page(executeCommand(pdftotextExecutablePath, "-layout", file.absolutePath, "-").output))
            return extractTextPageByPage(file)
        }

        return ExtractionResult(ErrorInfo(ErrorType.ExtractorNotAvailable))
    }

    protected open fun extractTextPageByPage(file: File): ExtractionResult {
        val result = ExtractionResult()

        generateSequence(1) { it + 1 }.forEach { pageNum ->
            val pageResult = extractPageText(file, pageNum)

            if (pageResult.successful) {
                result.addPage(Page(pageResult.output, pageNum))
            }
            else { // if pageNum is out of range exitCode 99 gets returned and error message is 'Command Line Error: Wrong page range given: the first page (<count pages>) can not be after the last page (<count pages + 1>).'
                return result
            }
        }

        return result // should never come to this
    }

    protected open fun extractPageText(file: File, pageNum: Int): ExecuteCommandResult {
        /**
         * pdftotext command line arguments:
         *  -f <int>: first page to convert
         *  -l <int>: last page to convert
         *  -layout: maintain original physical layout
         *  - (last parameter): print to console instead of to file
         */
        // TODO: add .exe to pdftotext / pdftotextExecutablePath on Windows
        return executeCommand(
            pdftotextExecutablePath,
            "-f",
            pageNum.toString(),
            "-l",
            pageNum.toString(),
            "-layout",
            file.absolutePath,
            "-"
        )
    }


    protected open fun executeCommand(vararg arguments: String): ExecuteCommandResult {
        try {
            // TODO: add "/bin/bash" or "cmd.exe" ?

            val processBuilder = ProcessBuilder(*arguments)

            val process = processBuilder.start()

            val outputReader = process.inputStream.bufferedReader()

            val processOutput = outputReader.readText().trim()

            val errorReader = process.errorStream.bufferedReader()

            val errors = errorReader.readText().trim()

            val exitCode = process.waitFor()
            log.debug("Command ${arguments.joinToString(" ")} exited with code $exitCode")

            outputReader.close()
            errorReader.close()

            return ExecuteCommandResult(exitCode, processOutput, errors)
        } catch (e: Exception) {
            log.error("Could not execute command ${arguments.joinToString(" ")}", e)

            return ExecuteCommandResult(-1, "", e.toString())
        }
    }

}