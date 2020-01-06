package net.dankito.text.extraction.invoice

import net.dankito.text.extraction.invoice.model.BankData
import net.dankito.text.extraction.invoice.model.StringSearchResult
import java.util.regex.Pattern


open class BankDataExtractor : IBankDataExtractor {

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
        const val BicPatternString = "[A-Z]{4}[A-Z]{2}[A-Z0-9]{2}[A-Z0-9]{0,3}"
        val BicPattern = Pattern.compile(BicPatternString)
    }


    override fun extractBankData(text: String): BankData {
        return extractBankData(text.split("\n"))
    }

    override fun extractBankData(lines: List<String>): BankData {
        return BankData(extractIbans(lines), extractBics(lines))
    }


    override fun extractIbans(text: String): List<StringSearchResult> {
        return extractIbans(text.split("\n"))
    }

    override fun extractIbans(lines: List<String>): List<StringSearchResult> {
        val result = mutableListOf<StringSearchResult>()

        result.addAll(findStrings(lines, IbanPattern))
        result.addAll(findStrings(lines, IbanWithSpacesPattern))

        return result
    }


    override fun extractBics(text: String): List<StringSearchResult> {
        return extractBics(text.split("\n"))
    }

    override fun extractBics(lines: List<String>): List<StringSearchResult> {
        return findStrings(lines, BicPattern)
    }


    protected open fun findStrings(lines: List<String>, pattern: Pattern): List<StringSearchResult> {
        val matchers = lines.associateBy({ it }, { pattern.matcher(it) })

        val matches = matchers.filter { it.value.find() }

        return matches.map { StringSearchResult(it.value.group(), it.key) }
    }

}