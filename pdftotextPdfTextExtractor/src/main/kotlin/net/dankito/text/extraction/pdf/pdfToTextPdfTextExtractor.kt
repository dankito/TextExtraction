package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ITextExtractor
import java.io.File
import net.dankito.text.extraction.model.ExtractedText
import net.dankito.text.extraction.model.Page
import org.slf4j.LoggerFactory


open class pdfToTextPdfTextExtractor @JvmOverloads constructor(protected val pdftotextExecutablePath: String = "pdftotext") : ITextExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(pdfToTextPdfTextExtractor::class.java)
    }


    // TODO: adjust for a) Windows b) if pdftotextExecutablePath is set
    protected val didFindPdftotextExecutable: Boolean = File(executeCommand("which", pdftotextExecutablePath).output).exists()

    override val isAvailable = didFindPdftotextExecutable


    override fun extractText(file: File): ExtractedText {
        if (isAvailable) {
            val result = ExtractedText()

            // TODO: add .exe to pdftotext / pdftotextExecutablePath on Windows
            result.addPage(Page(executeCommand(pdftotextExecutablePath, "-layout", file.absolutePath, "-").output))

            return result
        }

        return ExtractedText() // TODO: add error info to ExtractedText
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
            log.info("Command ${arguments.joinToString(" ")} exited with code $exitCode")

            outputReader.close()
            errorReader.close()

            return ExecuteCommandResult(exitCode, processOutput, errors)
        } catch (e: Exception) {
            log.error("Could not execute command ${arguments.joinToString(" ")}", e)

            return ExecuteCommandResult(-1, "", e.toString())
        }
    }

}