package net.dankito.text.extraction.image

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.dankito.text.extraction.ExecuteCommandResult
import net.dankito.text.extraction.image.model.OcrLanguage
import net.dankito.text.extraction.image.model.TesseractConfig
import net.dankito.utils.Stopwatch
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.concurrent.thread


class Tesseract4CommandLineImageTextExtractorTest {

    companion object {
//        private val TestImageFile = File("<insert path to your test pdf here>")
        private val TestImageFile = File("/tmp/apache-tika-8396270117089947981.png") // TODO: undo
    }


    private val underText = Tesseract4CommandLineImageTextExtractor(TesseractConfig(listOf(OcrLanguage.English, OcrLanguage.German)))


    @Ignore // TODO: undo
    @Test
    fun extractText() {

        // when
        val result = underText.extractText(TestImageFile)

        // then
        assertThat(result.pages).isNotEmpty
        assertThat(result.text).isEqualTo("<add your expected text here>")
    }


    // TODO: remove again:

    @Test
    fun `Recognize image`() {

        val result = executeCommand("tesseract ${TestImageFile.absolutePath} stdout -l deu hocr")

        assertThat(result.successful).isTrue()
        assertThat(result.output).isNotBlank()
        assertThat(result.errors).isBlank()
    }

    @Test
    fun `Recognize image parallel`() {

        val commandToRun = "tesseract ${TestImageFile.absolutePath} stdout -l deu hocr"

        thread { executeCommand(commandToRun) }
        thread { executeCommand(commandToRun) }
        thread { executeCommand(commandToRun) }
        thread { executeCommand(commandToRun) }
        thread { executeCommand(commandToRun) }
        thread { executeCommand(commandToRun) }
        thread { executeCommand(commandToRun) }

        val result = executeCommand(commandToRun)

        assertThat(result.successful).isTrue()
        assertThat(result.output).isNotBlank()
        assertThat(result.errors).isBlank()
    }

    @Test
    fun `Long running PDF 1`() {

        val result = executeCommand("pdftotext /home/ganymed/data/docs/test/2017_Harari_Homo_Deus.pdf -")

        assertThat(result.successful).isTrue()
        assertThat(result.output).isNotBlank()
        assertThat(result.errors).isBlank()
    }

    @Test
    fun `Long running PDF 2`() {

        val result = executeCommand("pdftotext /home/ganymed/data/docs/test/mdb_251607_woeffi_2017_web.pdf -")

        assertThat(result.successful).isTrue()
        assertThat(result.output).isNotBlank()
//        assertThat(result.errors).isBlank()
    }

    @Test
    fun `tesseract not installed`() {

        val result = executeCommand("tesseractXyz")

        assertThat(result.successful).isFalse()
        assertThat(result.output).isBlank()
        assertThat(result.errors).isNotBlank()
    }


    private fun executeCommand(command: String): ExecuteCommandResult {
//        Stopwatch.logDuration("executeCommand() for $command") {
//            underText.executeCommand(command, null)
//        }

        Stopwatch.logDuration("executeCommandAsync() for $command") {
            runBlocking { underText.executeCommandAsync(command, null).await() }
        }

        Stopwatch.logDuration("executeCommandInExtraThreads() for $command") {
            underText.executeCommandInExtraThreads(command, null)
        }

//        return underText.executeCommand(command, null)
        return underText.executeCommandInExtraThreads(command, null)
//        return runBlocking { underText.executeCommandAsync(command, null).await() }
    }

}