package net.dankito.text.extraction.pdf

import net.dankito.utils.process.CommandConfig
import net.dankito.utils.process.CommandExecutor
import net.dankito.utils.process.ICommandExecutor
import java.io.File
import java.util.*


open class pdfToTextPdfTextExtractor @JvmOverloads constructor(
    osIndependentDefaultExecutableName: String = "pdftotext",
    commandExecutor: ICommandExecutor = CommandExecutor(),
    metadataExtractor: IPdfMetadataExtractor = pdfinfoPdfMetadataExtractor(commandExecutor),
    /**
     * To speed up processing pdfToTextPdfTextExtractor by default reads a PDF's pages in parallel. If now multiple
     * pdfToTextPdfTextExtractor instances run in parallel, this would consume to many CPU resources and your system
     * therefore would go down.
     */
    willMultipleInstancesRunInParallel: Boolean = false,
    /**
     * Only needed for UI applications that like to show an hint to user when external application isn't found.
     */
    installHintLocalization: ResourceBundle = ResourceBundle.getBundle("Messages")
) : pdfToTextPdfTextExtractorBase(osIndependentDefaultExecutableName, commandExecutor, metadataExtractor, willMultipleInstancesRunInParallel, installHintLocalization), ISearchablePdfTextExtractor {


    override val name = "pdftotext"


    override fun createCommandConfig(file: File, pageNum: Int): CommandConfig {
        /**
         * pdftotext command line arguments:
         *  -f <int>: first page to convert
         *  -l <int>: last page to convert
         *  -layout: maintain original physical layout
         *  - (last parameter): print to console instead of to file
         */
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