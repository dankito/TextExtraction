package net.dankito.text.extraction.pdf

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File


class itextPdfTextExtractorTest {

    companion object {
        private val TestPdfFile = File("<insert path to your test pdf here>")
    }


    private val underText = itextPdfTextExtractor()


    @Test
    fun extractText() {

        // when
        val result = underText.extractText(TestPdfFile)

        // then
        assertThat(result.pages).isNotEmpty
        assertThat(result.text).isEqualTo("<add your expected text here>")
    }

}