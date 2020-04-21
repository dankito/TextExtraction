package net.dankito.text.extraction.image

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.image.model.OcrLanguage
import org.junit.jupiter.api.Disabled
import java.io.File


@Disabled
class FineReaderHotFolderImageTextExtractorTest : ImageTextExtractorTestBase() {

    companion object {
        // set your Abby FineReader HotFolder path here
        val FineReaderHotFolderPath = File("")

        val FineReaderHotFolderOutputPath = FineReaderHotFolderPath
    }


    override fun createExtractorForLanguage(language: OcrLanguage): ITextExtractor {
        return FineReaderHotFolderImageTextExtractor(
            FineReaderHotFolderConfig(FineReaderHotFolderPath, FineReaderHotFolderOutputPath))
    }

}