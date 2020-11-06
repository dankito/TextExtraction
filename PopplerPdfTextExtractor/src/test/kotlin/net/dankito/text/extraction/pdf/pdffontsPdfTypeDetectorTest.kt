package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.TextExtractorType
import net.dankito.text.extraction.model.PdfType
import net.dankito.text.extraction.util.TestFilesHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class pdffontsPdfTypeDetectorTest {

    private val testFilesHelper = TestFilesHelper()

    private val underTest = pdffontsPdfTypeDetector()


    @Test
    fun detectPdfType_ImageOnlyPdf() {

        // given
        val imageFile = testFilesHelper.getEnglishTestFile(TextExtractorType.ImageOnlyPdf)

        // when
        val result = underTest.detectPdfType(imageFile)

        // then
        assertThat(result).isEqualTo(PdfType.ImageOnlyPdf)
    }

    @Test
    fun detectPdfType_SearchableTextPdf() {

        // given
        val imageFile = testFilesHelper.getEnglishTestFile(TextExtractorType.SearchableTextPdf)

        // when
        val result = underTest.detectPdfType(imageFile)

        // then
        assertThat(result).isEqualTo(PdfType.SearchableTextPdf)
    }

    @Test
    fun detectPdfType_NotAPdf() {

        // given
        val imageFile = testFilesHelper.getEnglishTestFile(TextExtractorType.Image)

        // when
        val result = underTest.detectPdfType(imageFile)

        // then
        assertThat(result).isNull()
    }

}