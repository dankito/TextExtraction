package net.dankito.text.extraction.image

import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.model.ExtractedText
import net.dankito.text.extraction.model.Page
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.leptonica.global.lept.pixDestroy
import org.bytedeco.leptonica.global.lept.pixRead
import org.bytedeco.tesseract.TessBaseAPI
import org.slf4j.LoggerFactory
import java.io.File


open class Tesseract4ImageTextExtractor : ITextExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(Tesseract4ImageTextExtractor::class.java)
    }


    protected val isTesseract4Installed: Boolean

    open val tessdataDirectory = File("tessdata")


    init {
        isTesseract4Installed = determineIsTesseract4Installed()
    }


    override val isAvailable = isTesseract4Installed


    override fun extractText(file: File): ExtractedText {
        if (isAvailable) {
            try {
                val api = TessBaseAPI()

                if (api.Init(tessdataDirectory.absolutePath, null) != 0) { // TODO: set language
                    log.error("Could not initialize tesseract.")
                    api.End()
                    return ExtractedText() // TODO: add error info to ExtractedText
                }

                val result = ExtractedText()

                // Open input image with leptonica library
                val image = pixRead(file.absolutePath)
                api.SetImage(image)

                // Get OCR result
                val outText: BytePointer? = api.GetUTF8Text()

                outText?.string?.let { result.addPage(Page(it)) }

                // Destroy used object and release memory
                api.End()
                outText?.deallocate()
                pixDestroy(image)

                return result
            } catch (e: Exception) {
                log.error("Could not recognize text of file $file", e)
                return ExtractedText() // TODO: add error info to ExtractedText
            }
        }

        return ExtractedText() // TODO: add error info to ExtractedText
    }


    protected open fun determineIsTesseract4Installed(): Boolean {
        val api = TessBaseAPI()

        val isTesseract4Installed = api.Init(tessdataDirectory.absolutePath, null) == 0

        api.End()

        return isTesseract4Installed
    }

}