package net.dankito.text.extraction.image.model

import java.io.File


class Tesseract4Config @JvmOverloads constructor(
    val tessdataDirectory: File = File("tessdata"),
    val languages: List<String>? = null,
    val pageSegMode: PageSegMode? = null
) {
}