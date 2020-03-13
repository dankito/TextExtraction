package net.dankito.text.extraction.image

import net.dankito.text.extraction.ITextExtractor.Companion.TextExtractionQualityForUnsupportedFileType
import net.dankito.text.extraction.TextExtractorBase
import net.dankito.text.extraction.image.model.TesseractConfig
import net.dankito.text.extraction.image.model.TesseractHelper
import net.dankito.text.extraction.model.ErrorInfo
import net.dankito.text.extraction.model.ErrorType
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Page
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.leptonica.global.lept.pixDestroy
import org.bytedeco.leptonica.global.lept.pixRead
import org.bytedeco.tesseract.TessBaseAPI
import java.io.File


open class Tesseract4JniImageTextExtractor @JvmOverloads constructor(
    protected val config: TesseractConfig,
    protected val tesseractHelper: TesseractHelper = TesseractHelper()
) : TextExtractorBase(), IImageTextExtractor, AutoCloseable {


    override val name = "Tesseract 4 JNI"

    override val isAvailable: Boolean

    override val supportedFileTypes = TesseractHelper.SupportedFileTypes

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
        // TODO: enable output type hOCR
//        val outText: BytePointer? = if (config.ocrOutputType == OcrOutputType.Hocr) api.GetHOCRText() else api.GetUTF8Text()

        val outText: BytePointer? = api.GetUTF8Text()

        outText?.string?.let {
            result.addPage(Page(it))

            outText.deallocate()
        }

        // Destroy used object and release memory
        pixDestroy(image)

        return result
    }


    protected open fun initTesseract(api: TessBaseAPI, config: TesseractConfig): Boolean {
        val languagesString = tesseractHelper.getTesseractLanguageString(config.ocrLanguages)
        val tessdataDirectory = config.tessdataDirectory ?: File("tessdata")

        val isTesseract4Installed = api.Init(tessdataDirectory.absolutePath, languagesString) == 0

        config.pageSegMode?.let { pageSegMode ->
            api.SetPageSegMode(pageSegMode.mode)
        }

        return isTesseract4Installed
    }

}