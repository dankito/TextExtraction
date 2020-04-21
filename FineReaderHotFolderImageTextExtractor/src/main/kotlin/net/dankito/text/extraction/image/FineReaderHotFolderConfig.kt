package net.dankito.text.extraction.image

import java.io.File


open class FineReaderHotFolderConfig(
    var hotFolderPath: File,
    var outputFolder: File
) {

    override fun toString(): String {
        return "$hotFolderPath ($outputFolder)"
    }

}