package net.dankito.text.extraction.image

import kotlinx.coroutines.*
import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.image.model.OcrLanguage
import net.dankito.text.extraction.image.model.TesseractConfig
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.utils.process.CpuInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class Tesseract4CommandlineImageTextExtractorTest : ImageTextExtractorTestBase() {

    override fun createExtractorForLanguage(language: OcrLanguage): ITextExtractor {
        return Tesseract4CommandlineImageTextExtractor(TesseractConfig(listOf(language)))
    }


    // if base class wouldn't restrict count parallel calls to Tesseract, Tesseract would block
    // all your CPUs and this test therefore would kill your system
    @Test
    fun testParallelCalls() {

        // given
        val underTest = createExtractorForLanguage(OcrLanguage.English)
        val testFile = getEnglishTestFile()
        val countParallelInstances = CpuInfo.CountCores * 2

        val results = mutableListOf<ExtractionResult>()
        val countDownLatch = CountDownLatch(countParallelInstances)

        // when
        for (i in 0 until countParallelInstances) {
            thread {
                val result = underTest.extractText(testFile)
                if (result.couldExtractText) {
                    results.add(result)
                }

                countDownLatch.countDown()
            }

            TimeUnit.MILLISECONDS.sleep(100) // give it some time so that Tesseract4CommandlineImageTextExtractor can detect that there's already another Tesseract instance running
        }

        countDownLatch.await()

        // then
        assertThat(results).hasSize(countParallelInstances)
    }

    @Test
    fun testParallelCallsSuspendable() {

        // given
        val underTest = createExtractorForLanguage(OcrLanguage.English)
        val testFile = getEnglishTestFile()
        val countParallelInstances = CpuInfo.CountCores * 2

        val jobs = mutableListOf<Deferred<ExtractionResult>>()
        val results = mutableListOf<ExtractionResult>()

        // when
        runBlocking {
            for (i in 0 until countParallelInstances) {
                jobs.add(async(Dispatchers.IO) {
                    underTest.extractTextSuspendable(testFile)
                })

                delay(100) // give it some time so that Tesseract4CommandlineImageTextExtractor can detect that there's already another Tesseract instance running
            }

            results.addAll(jobs.map { it.await() }.filter { it.couldExtractText })
        }

        // then
        assertThat(results).hasSize(countParallelInstances)
    }

}