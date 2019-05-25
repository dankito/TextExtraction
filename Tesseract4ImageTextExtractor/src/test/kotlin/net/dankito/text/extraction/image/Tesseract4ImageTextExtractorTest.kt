package net.dankito.text.extraction.image

import net.dankito.text.extraction.image.model.Tesseract4Config
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Test
import java.io.File


class Tesseract4ImageTextExtractorTest {

    companion object {
        private val TestPdfFile = File("<insert path to your test pdf here>")
    }


    private val underText = Tesseract4ImageTextExtractor(Tesseract4Config())


    @After
    fun tearDown() {
        underText.close()
    }


    @Test
    fun extractText() {

        // when
        val result = underText.extractText(TestPdfFile)

        // then
        assertThat(result.pages).isNotEmpty
        assertThat(result.text).isEqualTo("<add your expected text here>")
    }

}