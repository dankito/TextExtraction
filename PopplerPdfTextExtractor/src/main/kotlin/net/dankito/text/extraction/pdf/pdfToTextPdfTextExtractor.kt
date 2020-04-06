package net.dankito.text.extraction.pdf

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import net.dankito.text.extraction.ExternalToolTextExtractorBase
import net.dankito.text.extraction.ITextExtractor.Companion.TextExtractionQualityForUnsupportedFileType
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Page
import net.dankito.utils.process.CommandConfig
import net.dankito.utils.process.CommandExecutor
import net.dankito.utils.process.CpuInfo
import net.dankito.utils.process.ICommandExecutor
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


open class pdfToTextPdfTextExtractor @JvmOverloads constructor(
    commandExecutor: ICommandExecutor = CommandExecutor(),
    protected val metadataExtractor: IPdfMetadataExtractor = pdfinfoPdfMetadataExtractor(commandExecutor),
    /**
     * To speed up processing pdfToTextPdfTextExtractor by default reads a PDF's pages in parallel. If now multiple
     * pdfToTextPdfTextExtractor instances run in parallel, this would consume to many CPU resources and your system
     * therefore would go down.
     */
    val willMultipleInstancesRunInParallel: Boolean = false
) : ExternalToolTextExtractorBase("pdftotext", commandExecutor, UnlimitedParallelExecutions), ISearchablePdfTextExtractor {



    protected val extractPagesParallelExecutor: ExecutorService = Executors.newFixedThreadPool(CpuInfo.CountCores * 10)


    override val name = "pdftotext"

    override val supportedFileTypes = listOf("pdf")

    override val installHint = getInstallHintForOsType("error.message.poppler.utils.application.not.found.")

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

        if (extractedLength > 0) { // then we now how many pages the PDF has
            if (willMultipleInstancesRunInParallel == false) {
                extractPagesInParallel(extractedLength, file, result)
            }
            else {
                for (pageNum in 1..extractedLength) {
                    extractPage(file, result, pageNum)
                }
            }
        }
        else { // otherwise we will have to guess / find out the hard way how many pages it has
            generateSequence(1) { it + 1 }.forEach { pageNum ->
                if (extractPage(file, result, pageNum) == false) { // if pageNum is out of range exitCode 99 gets returned and error message is 'Command Line Error: Wrong page range given: the first page (<count pages>) can not be after the last page (<count pages + 1>).'
                    return result
                }
            }
        }

        return result // should never come to this
    }

    protected open fun extractPagesInParallel(countPages: Int, file: File, result: ExtractionResult) {
        val countDownLatch = CountDownLatch(countPages)

        for (pageNum in 1..countPages) {
            extractPagesParallelExecutor.submit {
                extractPage(file, result, pageNum)

                countDownLatch.countDown()
            }
        }

        try { countDownLatch.await(3, TimeUnit.MINUTES) } catch (ignored: Exception) { }
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

        if (extractedLength > 0) { // then we now how many pages the PDF has
            if (willMultipleInstancesRunInParallel) {
                for (pageNum in 1..extractedLength) {
                    extractPageSuspendable(file, result, pageNum)
                }
            }
            else {
                val dispatcher = extractPagesParallelExecutor.asCoroutineDispatcher()
                coroutineScope {
                    for (pageNum in 1..extractedLength) {
                        async(dispatcher) { extractPageSuspendable(file, result, pageNum) }
                    }
                }
            }
        }
        else { // otherwise we will have to guess / find out the hard way how many pages it has
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