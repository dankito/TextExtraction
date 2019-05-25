package net.dankito.text.extraction.image

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.image.model.Tesseract4Config
import net.dankito.text.extraction.model.ExtractedText
import net.dankito.text.extraction.model.Page
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.leptonica.global.lept.pixDestroy
import org.bytedeco.leptonica.global.lept.pixRead
import org.bytedeco.tesseract.TessBaseAPI
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.File


open class Tesseract4ImageTextExtractor(config: Tesseract4Config) : ITextExtractor, Closeable {

    companion object {
        private val log = LoggerFactory.getLogger(Tesseract4ImageTextExtractor::class.java)
    }


    override val isAvailable: Boolean


    protected val api = TessBaseAPI()


    init {
        this.isAvailable = initTesseract(api, config)
    }


    override fun close() {
        api.End()
    }


    override fun extractText(file: File): ExtractedText {
        if (isAvailable) {
            try {

                val result = ExtractedText()

                // Open input image with leptonica library
                val image = pixRead(file.absolutePath)
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
            } catch (e: Exception) {
                log.error("Could not recognize text of file $file", e)
                return ExtractedText() // TODO: add error info to ExtractedText
            }
        }

        return ExtractedText() // TODO: add error info to ExtractedText
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