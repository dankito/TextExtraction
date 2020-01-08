package net.dankito.text.extraction.info.bank

import net.dankito.text.extraction.info.ExtractorBase
import net.dankito.text.extraction.info.model.StringSearchResult
import java.util.regex.Pattern


open class BicExtractor : ExtractorBase(), IBicExtractor {

    companion object {
        /**
         * The SWIFT code is 8 or 11 characters, made up of:
         * - 4 letters: institution code or bank code.
         * - 2 letters: ISO 3166-1 alpha-2 country code (exceptionally, SWIFT has assigned the code XK to Republic of Kosovo, which does not have an ISO 3166-1 country code)
         * - 2 letters or digits: location code
         * -- if the second character is "0", then it is typically a test BIC as opposed to a BIC used on the live network.
         * -- if the second character is "1", then it denotes a passive participant in the SWIFT network
         * -- if the second character is "2", then it typically indicates a reverse billing BIC, where the recipient pays for the message as opposed to the more usual mode whereby the sender pays for the message.
         * - 3 letters or digits: branch code, optional ('XXX' for primary office)
         * Where an eight digit code is given, it may be assumed that it refers to the primary office.
         */
        const val BicPatternString = "\\b[A-Z]{4}[A-Z]{2}[A-Z0-9]{2}[A-Z0-9]{0,3}\\b"
        val BicPattern = Pattern.compile(BicPatternString)
    }


    override fun extractBics(text: String): List<StringSearchResult> {
        return extractBics(getLines(text))
    }

    override fun extractBics(lines: List<String>): List<StringSearchResult> {
        return findStrings(lines, BicPattern)
    }

}