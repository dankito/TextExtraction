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
    protected val didFindPdftotextExecutable: Boolean = File(executeCommand("which", pdftotextExecutablePath)).exists()

    override val isAvailable = didFindPdftotextExecutable


    override fun extractText(file: File): ExtractedText {
        if (isAvailable) {
            val result = ExtractedText()

            // TODO: add .exe to pdftotext / pdftotextExecutablePath on Windows
            result.addPage(Page(executeCommand(pdftotextExecutablePath, "-layout", file.absolutePath, "-")))

            return result
        }

        return ExtractedText() // TODO: add error info to ExtractedText
    }


    protected open fun executeCommand(vararg arguments: String): String {
        try {
            // TODO: add "/bin/bash" or "cmd.exe" ?

            val processBuilder = ProcessBuilder(*arguments)

            val process = processBuilder.start()

            val reader = process.inputStream.bufferedReader()

            val processOutput = reader.readText().trim()

            val exitCode = process.waitFor()
            log.info("Command ${arguments.joinToString(" ")} exited with code $exitCode")

            reader.close()

            return processOutput
        } catch (e: Exception) {
            log.error("Could not execute command ${arguments.joinToString(" ")}", e)
        }

        return "" // TODO: what to return in this case? String?
    }

}