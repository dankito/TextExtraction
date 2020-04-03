package net.dankito.text.extraction.image

import net.dankito.text.extraction.TextExtractorTestBase
import net.dankito.text.extraction.TextExtractorType


abstract class ImageTextExtractorTestBase : TextExtractorTestBase() {

    override val textExtractorType = TextExtractorType.Image

}