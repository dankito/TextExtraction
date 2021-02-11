package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.model.Page
import net.dankito.utils.process.CommandConfig
import net.dankito.utils.process.CommandExecutor
import net.dankito.utils.process.ExecuteCommandResult
import net.dankito.utils.process.ICommandExecutor
import java.io.File
import java.util.*


open class pdfToHtmlPdfTextExtractor @JvmOverloads constructor(
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
) : pdfToTextPdfTextExtractorBase("pdftohtml", commandExecutor, metadataExtractor, willMultipleInstancesRunInParallel, installHintLocalization), ISearchablePdfTextExtractor {


    override val name = "pdftohtml"


    override fun createCommandConfig(file: File, pageNum: Int): CommandConfig {
        /**
         * pdftohtml command line arguments:
         *  -f <int>: first page to convert
         *  -l <int>: last page to convert
         *  -nomerge: do not merge paragraphs. Better retains real layout
         *  -c: generate complex document; not sure if we should use it or not?
         *  -p: exchange .pdf links by .html
         *  -s: generate single document that includes all pages instead of one html file for each page
         *  -noframes: by default pdftohtml generates a <name>_previews.html file that includes pages in frames
         *  -fontfullname: outputs font full name; don't know if it really makes a difference using this parameter
         *  -fmt png: By default pdftohtml generates to preview image as .jpg.
         *  -dataurls: embeds images into html instead of linking to external images.
         *  result.html (last parameter): output file name
         */

        // -s -noframes -fmt png -fontfullname -nomerge -p -c -dataurls
        return CommandConfig(listOf(
            commandlineProgram.programExecutablePath,
            "-f",
            pageNum.toString(),
            "-l",
            pageNum.toString(),
            "-nomerge",
            "-c",
            "-p",
            "-s",
            "-noframes",
            "-fontfullname",
            "-fmt",
            "png",
            "-dataurls",
            file.absolutePath,
            getPageFilename(pageNum)
        ))
    }


    override fun createPage(pageResult: ExecuteCommandResult, pageNum: Int): Page {
        val pageFile = File(getPageFilename(pageNum))

        val pageHtml = pageFile.readText()

        pageFile.delete()

        return Page(pageHtml, pageNum)
    }


    protected open fun getPageFilename(pageNum: Int) = "result_$pageNum.html"

}