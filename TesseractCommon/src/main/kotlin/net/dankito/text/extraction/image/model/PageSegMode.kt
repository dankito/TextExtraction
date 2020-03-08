package net.dankito.text.extraction.image.model


/**
 * Copied from Tesseract publictypes.h (https://github.com/tesseract-ocr/tesseract/blob/b1e305f38c7771f483cf314d6b5552cdf6222978/src/ccstruct/publictypes.h).
 */
enum class PageSegMode(val mode: Int) {

    /**
     * Orientation and script detection only.
     */
    PSM_OSD_ONLY(0),

    /**
     * Automatic page segmentation with orientation and script detection. (OSD)
     */
    PSM_AUTO_OSD(1),

    /**
     * Automatic page segmentation, but no OSD, or OCR.
     */
    PSM_AUTO_ONLY(2),
    /**
     * Fully automatic page segmentation, but no OSD.
     */
    PSM_AUTO(3),

    /**
     * Assume a single column of text of variable sizes.
     */
    PSM_SINGLE_COLUMN(4),

    /**
     * Assume a single uniform block of vertically aligned text.
     */
    PSM_SINGLE_BLOCK_VERT_TEXT(5),

    /**
     * Assume a single uniform block of text. (Default.)
     */
    PSM_SINGLE_BLOCK(6),

    /**
     * Treat the image as a single text line.
     */
    PSM_SINGLE_LINE(7),

    /**
     * Treat the image as a single word.
     */
    PSM_SINGLE_WORD(8),

    /**
     * Treat the image as a single word in a circle.
     */
    PSM_CIRCLE_WORD(9),

    /**
     * Treat the image as a single character.
     */
    PSM_SINGLE_CHAR(10),

    /**
     * Find as much text as possible in no particular order.
     */
    PSM_SPARSE_TEXT(11),

    /**
     * Sparse text with orientation and script det.
     */
    PSM_SPARSE_TEXT_OSD(12),

    /**
     * Treat the image as a single text line, bypassing hacks that are Tesseract-specific.
     */
    PSM_RAW_LINE(13),

}