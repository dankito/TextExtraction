package net.dankito.text.extraction.info.bank

import net.dankito.text.extraction.info.ExtractorBase
import net.dankito.text.extraction.info.model.StringSearchResult
import java.util.regex.Pattern


open class IbanExtractor : ExtractorBase(), IIbanExtractor {

    companion object {
        /**
         * The IBAN consists of up to 34 alphanumeric characters, as follows:
         * - country code using ISO 3166-1 alpha-2 – two letters,
         * - check digits – two digits, and
         * - Basic Bank Account Number (BBAN) – up to 30 alphanumeric characters that are country-specific.
         * (https://en.wikipedia.org/wiki/International_Bank_Account_Number#Structure)
         */
        const val IbanPatternString = "[A-Z]{2}\\d{2}[A-Z0-9]{10,30}"
        val IbanPattern = Pattern.compile(IbanPatternString)

        /**
         * The IBAN should not contain spaces when transmitted electronically. When printed it is expressed in groups
         * of four characters separated by a single space, the last group being of variable length as shown in the example below
         * (https://en.wikipedia.org/wiki/International_Bank_Account_Number#Structure)
         */
        const val IbanWithSpacesPatternString = "[A-Z]{2}\\d{2}\\s([A-Z0-9]{4}\\s){3}[A-Z0-9\\s]{1,18}"
        val IbanWithSpacesPattern = Pattern.compile(IbanWithSpacesPatternString)
    }


    override fun extractIbans(text: String): List<StringSearchResult> {
        return extractIbans(getLines(text))
    }

    override fun extractIbans(lines: List<String>): List<StringSearchResult> {
        val result = mutableListOf<StringSearchResult>()

        result.addAll(findStrings(lines, IbanPattern))
        result.addAll(findStrings(lines, IbanWithSpacesPattern))

        return result
    }

}