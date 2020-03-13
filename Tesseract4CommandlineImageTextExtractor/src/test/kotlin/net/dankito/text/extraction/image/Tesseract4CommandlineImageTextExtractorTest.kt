package net.dankito.text.extraction.image

import net.dankito.text.extraction.image.model.OcrLanguage
import net.dankito.text.extraction.image.model.TesseractConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.Test
import java.io.File


class Tesseract4CommandlineImageTextExtractorTest {

    companion object {
        private val TestImageFile = File("<insert path to your test pdf here>")
    }


    private val underText = Tesseract4CommandlineImageTextExtractor(TesseractConfig(listOf(OcrLanguage.English, OcrLanguage.German)))


    @Ignore
    @Test
    fun extractText() {

        // when
        val result = underText.extractText(TestImageFile)

        // then
        assertThat(result.pages).isNotEmpty
        assertThat(result.text).isEqualTo("<add your expected text here>")
    }

}