package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.TextExtractorBase
import net.dankito.text.extraction.image.IImageTextExtractor
import net.dankito.text.extraction.model.ErrorInfo
import net.dankito.text.extraction.model.ErrorType
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Page
import java.io.File


/**
 * An [ITextExtractor] that extracts texts from images embedded in PDFs by first extracts all images
 * from PDF file with a [IImagesFromPdfExtractor] and then applies OCR on extracted images with [IImageTextExtractor].
 */
open class ImageBasedPdfTextExtractor(
    protected val imageTextExtractor: IImageTextExtractor,
    protected val imagesFromPdfExtractor: IImagesFromPdfExtractor
) : TextExtractorBase(), IImageBasedPdfTextExtractor {


    override val name: String
        get() = "Extract text from images embedded in PDFs"

    override val isAvailable = true

    override val supportedFileTypes = listOf("pdf")


    override fun getTextExtractionQualityForFileType(file: File): Int {
        val virtualImageFromPdf = File(file.parentFile, file.nameWithoutExtension + ".tiff")

        return imageTextExtractor.getTextExtractionQualityForFileType(virtualImageFromPdf) // text extraction quality has to be measured for images, not PDFs
    }

    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        val extractedImages = imagesFromPdfExtractor.extractImages(file)

        if (extractedImages.isSuccessful == false) {
            return ExtractionResult(ErrorInfo(ErrorType.ParseError, extractedImages.error))
        }

        val result = ExtractionResult()
        var firstError: Exception? = null

        extractedImages.extractedImages.forEachIndexed { pageNum, imageFile -> // usually there's one image per page in PDF -> we can use index as page number
            val extractedText = imageTextExtractor.extractText(imageFile)

            if (extractedText.couldExtractText) {
                extractedText.pages.forEach { page ->
                    result.addPage(Page(page.text, if (page.isPageNumSet) page.pageNum else pageNum))
                }
            }
            else if (firstError == null && extractedText.error != null) {
                firstError = extractedText.error.exception
            }
        }

        if (firstError != null) {
            return ExtractionResult(ErrorInfo(ErrorType.ParseError, firstError))
        }

        return result
    }

}