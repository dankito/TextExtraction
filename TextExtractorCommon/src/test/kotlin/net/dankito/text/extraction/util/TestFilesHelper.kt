package net.dankito.text.extraction.util

import net.dankito.text.extraction.TextExtractorTestBase
import net.dankito.text.extraction.TextExtractorType
import net.dankito.utils.io.FileUtils
import net.dankito.utils.resources.ResourceFilesExtractor
import java.io.File


open class TestFilesHelper {

    companion object {
        const val TestFilesFolderName = "test_files"

        const val WikipediaGandhiEnglishTestFileBaseName = "Wikipedia_Gandhi_en"
        const val WikipediaGandhiGermanTestFileBaseName = "Wikipedia_Gandhi_de"
    }


    protected val fileUtils = FileUtils()

    protected val resourceFilesExtractor = ResourceFilesExtractor()


    open fun getEnglishTestFile(textExtractorType: TextExtractorType): File {
        return getTestFile(WikipediaGandhiEnglishTestFileBaseName, textExtractorType)
    }

    open fun getGermanTestFile(textExtractorType: TextExtractorType): File {
        return getTestFile(WikipediaGandhiGermanTestFileBaseName, textExtractorType)
    }

    open fun getTestFile(filename: String, extractorType: TextExtractorType): File {
        val isImageTextExtractor = extractorType == TextExtractorType.Image

        val resourceFolderName = if (isImageTextExtractor) "images" else "pdf"
        val testFilename = determineTestFileName(filename, extractorType, isImageTextExtractor)

        val testFile = File(File(TestFilesFolderName, resourceFolderName), testFilename)

        val fileUrl = TextExtractorTestBase::class.java.classLoader.getResource(testFile.path)

        if ("jar" == fileUrl.protocol) { // if running from jar file
            val tempFolder = fileUtils.getTempDir() // extract test pdfs to temp folder

            resourceFilesExtractor.extractAllFilesFromFolder(
                TextExtractorTestBase::class.java,
                File(TestFilesFolderName), tempFolder
            )

            return File(tempFolder, testFilename)
        } else {
            return File(fileUrl.toURI())
        }
    }

    protected open fun determineTestFileName(filename: String, extractorType: TextExtractorType, isImageTextExtractor: Boolean): String {
        var testFilename = filename

        if (extractorType == TextExtractorType.SearchableTextPdf) {
            testFilename = insertModifierBeforeLanguage(testFilename, "_searchable_text")
        }
        else if (extractorType == TextExtractorType.ImageOnlyPdf) {
            testFilename = insertModifierBeforeLanguage(testFilename, "_image_only")
        }

        testFilename = if (isImageTextExtractor || extractorType == TextExtractorType.ImageOnlyPdf) testFilename + "_01" else testFilename

        val fileExtension = if (isImageTextExtractor) "png" else "pdf"

        return testFilename + "." + fileExtension
    }

    protected open fun insertModifierBeforeLanguage(filename: String, modifier: String): String {
        val lastIndexOfUnderscore = filename.lastIndexOf('_')

        return filename.substring(0, lastIndexOfUnderscore) + modifier + filename.substring(lastIndexOfUnderscore)
    }

}