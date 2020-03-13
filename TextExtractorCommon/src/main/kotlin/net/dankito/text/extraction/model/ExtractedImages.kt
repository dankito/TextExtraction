package net.dankito.text.extraction.model

import java.io.File


open class ExtractedImages(val extractedImages: List<File>, val error: Exception? = null) {

    open val isSuccessful = error == null


    override fun toString(): String {
        if (isSuccessful) {
            return "Success, extracted ${extractedImages.size} images"
        }

        return "Error: $error"
    }

}