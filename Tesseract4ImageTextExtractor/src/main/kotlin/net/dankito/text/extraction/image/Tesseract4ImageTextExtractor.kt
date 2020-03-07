package net.dankito.text.extraction.image

import net.dankito.text.extraction.ITextExtractor.Companion.TextExtractionQualityForUnsupportedFileType
import net.dankito.text.extraction.TextExtractorBase
import net.dankito.text.extraction.image.model.Tesseract4Config
import net.dankito.text.extraction.model.ErrorInfo
import net.dankito.text.extraction.model.ErrorType
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Page
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.leptonica.global.lept.pixDestroy
import org.bytedeco.leptonica.global.lept.pixRead
import org.bytedeco.tesseract.TessBaseAPI
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.File


open class Tesseract4ImageTextExtractor(config: Tesseract4Config) : TextExtractorBase(), Closeable {

    override val isAvailable: Boolean

    override val supportedFileTypes = listOf("png", "jpg", "tif", "tiff") // set all supported file types

    override fun getTextExtractionQualityForFileType(file: File): Int {
        if (isFileTypeSupported(file)) {
            return 50
        }

        return TextExtractionQualityForUnsupportedFileType
    }


    protected val api = TessBaseAPI()


    init {
        this.isAvailable = initTesseract(api, config)
    }


    override fun close() {
        api.End()
    }


    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        // Open input image with leptonica library
        val image = pixRead(file.absolutePath)

        if (image == null) {
            return ExtractionResult(ErrorInfo(ErrorType.FileTypeNotSupportedByExtractor)) // image not found / openable / unsupported type
        }


        val result = ExtractionResult()

        api.SetImage(image)

        // Get OCR result
        val outText: BytePointer? = api.GetUTF8Text()

        outText?.string?.let {
            result.addPage(Page(it))

            outText.deallocate()
        }

        // Destroy used object and release memory
        pixDestroy(image)

        return result
    }


    protected open fun initTesseract(api: TessBaseAPI, config: Tesseract4Config): Boolean {
        val languagesString = config.languages?.joinToString("+")

        val isTesseract4Installed = api.Init(config.tessdataDirectory.absolutePath, languagesString) == 0

        config.pageSegMode?.let { pageSegMode ->
            api.SetPageSegMode(pageSegMode.mode)
        }

        return isTesseract4Installed
    }

}