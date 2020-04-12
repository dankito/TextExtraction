package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.model.PdfType
import net.dankito.utils.process.CommandExecutor
import net.dankito.utils.process.ICommandExecutor
import org.slf4j.LoggerFactory
import java.io.File


/**
 * Thanks so much Kurt Pfeifle for giving this superb hint (https://stackoverflow.com/a/3108531):
 * If a PDF uses fonts, than it contains (searchable) text.
 * If not, than PDF only contains images.
 *
 * So we can use `pdffonts` of Poppler utils to check how many fonts a PDF uses.
 */
open class pdffontsPdfTypeDetector(
    protected val commandExecutor: ICommandExecutor = CommandExecutor()
) : IPdfTypeDetector {

    companion object {
        private val log = LoggerFactory.getLogger(pdffontsPdfTypeDetector::class.java)
    }


    override fun detectPdfType(file: File): PdfType? {
        try {
            val executeCommandResult = commandExecutor.executeCommandWithLittleOutput(
                "pdffonts",
                file.absolutePath
            )

            // first two lines are headers -> if there are more then two lines than PDF uses fonts
            if (executeCommandResult.outputLines.size > 2) {
                return PdfType.SearchableTextPdf
            }
            else if (executeCommandResult.outputLines.size == 2) {
                return PdfType.ImageOnlyPdf
            }
            else {
                return null // not a PDF
            }
        } catch (e: Exception) {
            log.error("Could not get PDF type of file $file", e)
        }

        return null
    }

}