package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.model.Page
import net.dankito.utils.io.FileUtils
import net.dankito.utils.resources.ResourceFilesExtractor
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File


abstract class PdfTextExtractorTestBase {

    companion object {
        const val TestPdfFilesFolderName = "test-pdfs"

        const val MergeacroformsTestPdfFileName = "merge-acroforms.pdf"
    }


    abstract fun createExtractor(): ITextExtractor


    protected val underTest = createExtractor()

    protected val fileUtils = FileUtils()

    protected val resourceFilesExtractor = ResourceFilesExtractor()


    @Test
    fun extractText() {

        // given
        val testFile = getTestFile(MergeacroformsTestPdfFileName)

        // when
        val result = underTest.extractText(testFile)

        // then
        assertThat(result.pages).hasSize(16)
        assertThat(result.text).isNotEmpty()

        assertContainsIgnoringWhiteSpaces(result.pages.get(0), "Hello World")

        assertContainsIgnoringWhiteSpaces(result.pages.get(1), "1. This is chapter 1", "This is subsection 3 of section 2", "blah")

        assertContainsIgnoringWhiteSpaces(result.pages.get(3), "This is subsection 1 of section 3", "blah")

        assertContainsIgnoringWhiteSpaces(result.pages.get(7), "This is section 3 in chapter 3", "blah")

        assertContainsIgnoringWhiteSpaces(result.pages.get(10), "5. This is chapter 5", "This is subsection 2 of section 2", "blah")

        assertContainsIgnoringWhiteSpaces(result.pages.get(15), "3. This is section 3 in chapter 7", "blah")
    }


    protected open fun assertContainsIgnoringWhiteSpaces(page: Page, vararg stringsToContain: String) {
        assertThat(normalizeWhitespace(page.text)).contains(*stringsToContain)

        for (string in stringsToContain) {
        }
    }

    protected open fun normalizeWhitespace(toNormalize: CharSequence): String {
        val result = StringBuilder(toNormalize.length)
        var lastWasSpace = true

        for (i in 0 until toNormalize.length) {
            val c = toNormalize[i]
            if (Character.isWhitespace(c)) {
                if (!lastWasSpace) {
                    result.append(' ')
                }

                lastWasSpace = true
            } else {
                result.append(c)
                lastWasSpace = false
            }
        }

        return result.toString().trim { it <= ' ' }
    }


    protected open fun getTestFile(filename: String): File {
        val fileUrl = PdfTextExtractorTestBase::class.java.classLoader.getResource(File(TestPdfFilesFolderName, filename).path)

        if ("jar" == fileUrl.protocol) { // if running from jar file
            val tempFolder = fileUtils.getTempDir() // extract test pdfs to temp folder

            resourceFilesExtractor.extractAllFilesFromFolder(
                PdfTextExtractorTestBase::class.java,
                File(TestPdfFilesFolderName), tempFolder
            )

            return File(tempFolder, filename)
        } else {
            return File(fileUrl.toURI())
        }
    }

}