package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.model.Metadata
import net.dankito.utils.process.CommandExecutor
import net.dankito.utils.process.ExecuteCommandResult
import net.dankito.utils.process.ICommandExecutor
import org.slf4j.LoggerFactory
import java.io.File


open class pdfinfoPdfMetadataExtractor(
    protected val commandExecutor: ICommandExecutor = CommandExecutor()
) : IPdfMetadataExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(pdfinfoPdfMetadataExtractor::class.java)
    }


    override fun extractMetadata(file: File): Metadata? {
        try {
            val metadataResult = commandExecutor.executeCommandWithLittleOutput(
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