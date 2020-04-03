package net.dankito.text.extraction.metadata

import net.dankito.text.extraction.model.Metadata
import java.io.File


interface IFileMetadataExtractor {

    fun extractMetadata(file: File): Metadata?

}