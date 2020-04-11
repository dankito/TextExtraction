package net.dankito.text.extraction.pdf

import net.dankito.text.extraction.TextExtractorBase
import net.dankito.text.extraction.image.IImageTextExtractor
import net.dankito.text.extraction.model.*
import org.slf4j.LoggerFactory
import java.io.File


/**
 * An [ITextExtractor] that extracts texts from images embedded in PDFs by first extracting all images
 * from PDF file with a [IImagesFromPdfExtractor] and then applies OCR on extracted images with [IImageTextExtractor].
 */
open class ImageOnlyPdfTextExtractor(
    protected val imageTextExtractor: IImageTextExtractor,
    protected val imagesFromPdfExtractor: IImagesFromPdfExtractor
) : TextExtractorBase(), IImageOnlyPdfTextExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(ImageOnlyPdfTextExtractor::class.java)
    }


    override val name = "Extract text from images only PDFs"

    override val isAvailable = imageTextExtractor.isAvailable && imagesFromPdfExtractor.isAvailable

    override val supportedFileTypes = listOf("pdf")


    override fun getTextExtractionQualityForFileType(file: File): Int {
        val virtualImageFromPdf = File(file.parentFile, file.nameWithoutExtension + ".tiff")

        return imageTextExtractor.getTextExtractionQualityForFileType(virtualImageFromPdf) // text extraction quality has to be measured for images, not PDFs
    }


    override fun extractTextForSupportedFormat(file: File): ExtractionResult {
        val extractedImages = imagesFromPdfExtractor.extractImages(file)

        if (extractedImages.isSuccessful == false) {
            deleteExtractedImages(extractedImages)
            return ExtractionResult(ErrorInfo(ErrorType.ParseError, extractedImages.error))
        }


        val extractedTexts = extractedImages.extractedImages.map { imageFile ->
            imageTextExtractor.extractText(imageFile)
        }

        return mapToExtractionResult(extractedImages, extractedTexts)
    }

    // TODO: how to get rid of duplicated code?
    override suspend fun extractTextForSupportedFormatSuspendable(file: File): ExtractionResult {
        val extractedImages = imagesFromPdfExtractor.extractImagesSuspendable(file)

        if (extractedImages.isSuccessful == false) {
            deleteExtractedImages(extractedImages)
            return ExtractionResult(ErrorInfo(ErrorType.ParseError, extractedImages.error))
        }


        val extractedTexts = extractedImages.extractedImages.map { imageFile ->
            imageTextExtractor.extractTextSuspendable(imageFile)
        }

        return mapToExtractionResult(extractedImages, extractedTexts)
    }


    protected open fun mapToExtractionResult(extractedImages: ExtractedImages,
                                             extractedTexts: List<ExtractionResult>): ExtractionResult {
        val result = ExtractionResult(null, "application/pdf")
        var firstError: Exception? = null

        extractedTexts.forEachIndexed { pageNum, extractedText -> // usually there's one image per page in PDF -> we can use index as page number

            if (extractedText.couldExtractText) {
                extractedText.pages.forEach { page ->
                    result.addPage(Page(page.text, if (page.isPageNumSet) page.pageNum else pageNum))
                }

                if (result.metadata == null && extractedText.metadata != null) {
                    result.metadata = extractedText.metadata
                }
            }
            else if (firstError == null && extractedText.error != null) {
                firstError = extractedText.error?.exception
            }
        }

        deleteExtractedImages(extractedImages)

        if (firstError != null) {
            return ExtractionResult(ErrorInfo(ErrorType.ParseError, firstError))
        }

        return result
    }

    protected open fun deleteExtractedImages(extractedImages: ExtractedImages) {
        extractedImages.extractedImages.forEach { imageFile ->
            try {
                imageFile.delete()
            } catch (e: Exception) {
                log.warn("Could not delete extracted image file $imageFile", e)
            }
        }
    }

}