package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.commandline.CommandlineProgram
import net.dankito.text.extraction.model.ExtractedImages
import net.dankito.utils.process.CommandConfig
import net.dankito.utils.process.CommandExecutor
import net.dankito.utils.process.ExecuteCommandResult
import net.dankito.utils.process.ICommandExecutor
import java.io.File


open class pdfimagesImagesFromPdfExtractor(
    protected val commandExecutor: ICommandExecutor = CommandExecutor()
) : IImagesFromPdfExtractor {

    protected val commandlineProgram = CommandlineProgram("pdfimages", commandExecutor)

    override val isAvailable: Boolean
        get() = commandlineProgram.isAvailable


    override fun extractImages(pdfFile: File): ExtractedImages {
        val tmpDir = createTempImagesDestinationDirectory(pdfFile)

        val config = createCommandConfig(pdfFile, tmpDir)

        val result = commandExecutor.executeCommand(config)

        return mapResult(result, tmpDir)
    }

    override suspend fun extractImagesSuspendable(pdfFile: File): ExtractedImages {
        val tmpDir = createTempImagesDestinationDirectory(pdfFile)

        val config = createCommandConfig(pdfFile, tmpDir)

        val result = commandExecutor.executeCommandSuspendable(config)

        return mapResult(result, tmpDir)
    }


    protected open fun createTempImagesDestinationDirectory(pdfFile: File): File {
        val tmpDir = createTempDir("ExtractImagesFrom${pdfFile.nameWithoutExtension}", "")
        tmpDir.deleteOnExit()

        return tmpDir
    }

    protected open fun createCommandConfig(pdfFile: File, tmpDir: File): CommandConfig {
        val commandArgs = listOf(
            commandlineProgram.programExecutablePath,
            "-p", // add page number to file name
            "-tiff", // change the default output format to TIFF
            pdfFile.absolutePath,
            File(tmpDir, pdfFile.nameWithoutExtension).absolutePath
        )

        return CommandConfig(commandArgs)
    }

    protected open fun mapResult(result: ExecuteCommandResult, tmpDir: File): ExtractedImages {
        val extractedImages = tmpDir.listFiles().sortedBy { it.lastModified() }

        extractedImages.forEach { it.deleteOnExit() }

        if (result.successful == false) {
            return ExtractedImages(listOf(), Exception(result.errors))
        }

        return ExtractedImages(extractedImages)
    }

}