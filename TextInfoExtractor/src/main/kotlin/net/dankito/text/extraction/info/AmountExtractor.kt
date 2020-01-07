package net.dankito.text.extraction.info

import net.dankito.text.extraction.info.model.AmountOfMoney
import org.slf4j.LoggerFactory
import java.text.NumberFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


open class AmountExtractor(protected val currencySymbolPatternString: String = "\\p{Sc}|EUR",
                           protected val decimalNumberPatternString: String = "\\d+([\\,\\.]\\d{1,2})?"
) : IAmountExtractor {

    companion object {
        val UserNumberFormat = NumberFormat.getNumberInstance()

        // most languages have the English number format with a dot as decimal separator
        val NumberFormatWithDotAsDecimalSeparator = NumberFormat.getNumberInstance(Locale.ENGLISH)

        // German uses comma as decimal separator
        val NumberFormatWithCommaAsDecimalSeparator = NumberFormat.getNumberInstance(Locale.GERMAN)


        private val log = LoggerFactory.getLogger(AmountExtractor::class.java)
    }


    override fun extractAmountsOfMoney(lines: List<String>): List<AmountOfMoney> {
        val pattern = createCurrencySymbolPattern(currencySymbolPatternString)

        val matchers = lines.associateBy( { it } , { pattern.matcher(it) } )

        val matchersWithCurrencySymbols = matchers.filter { it.value.find() }

        return matchersWithCurrencySymbols.mapNotNull {
            extractAmountsOfMoney(it.value, it.key)
        }.flatten()
    }

    protected open fun extractAmountsOfMoney(matcherWithCurrencySymbol: Matcher, line: String): List<AmountOfMoney> {

        matcherWithCurrencySymbol.reset()
        var lineSubstringStart = 0

        val amounts = mutableListOf<AmountOfMoney>()

        while (matcherWithCurrencySymbol.find()) {
            extractAmountOfMoney(matcherWithCurrencySymbol, line, lineSubstringStart)?.let {
                amounts.add(it)
            }

            lineSubstringStart = matcherWithCurrencySymbol.end()
        }

        return amounts
    }

    protected open fun extractAmountOfMoney(matcherWithCurrencySymbol: Matcher, line: String, lineSubstringStart: Int): AmountOfMoney? {
        val relevantLinePart = line.substring(lineSubstringStart)
        val currencySymbol = matcherWithCurrencySymbol.group()


        val decimalNumberBeforeCurrencySymbolMatcher = createPatternForDecimalNumberBeforeCurrencySymbol(
            decimalNumberPatternString, currencySymbol
        ).matcher(relevantLinePart.substring(0, matcherWithCurrencySymbol.end() - lineSubstringStart))

        if (decimalNumberBeforeCurrencySymbolMatcher.find()) {
            return extractAmountOfMoney(decimalNumberBeforeCurrencySymbolMatcher, line, currencySymbol)
        }


        val decimalNumberAfterCurrencySymbolMatcher = createPatternForDecimalNumberAfterCurrencySymbol(
            decimalNumberPatternString, currencySymbol
        ).matcher(relevantLinePart.substring(matcherWithCurrencySymbol.start() - lineSubstringStart))

        if (decimalNumberAfterCurrencySymbolMatcher.find()) {
            return extractAmountOfMoney(decimalNumberAfterCurrencySymbolMatcher, line, currencySymbol)
        }

        return null
    }

    protected open fun extractAmountOfMoney(decimalNumberMatcher: Matcher, line: String, currencySymbol: String): AmountOfMoney {
        val amountWithCurrency = decimalNumberMatcher.group()
        val amount = extractAmount(amountWithCurrency, currencySymbol)

        return AmountOfMoney(amount, currencySymbol, amountWithCurrency, line)
    }

    protected open fun extractAmount(amountWithCurrency: String, currencySymbol: String): Double {
        val amountString = amountWithCurrency.replace(currencySymbol, "").trim()

        return extractNumber(amountString)?.toDouble() ?: amountString.toDouble()
    }


    override fun extractPercentages(lines: List<String>): List<AmountOfMoney> {
        val percentageSymbol = getPercentageSymbol()
        val percentagesPattern = createPatternForPercentage(decimalNumberPatternString, percentageSymbol)

        val matchers = lines.associateBy( { it } , { percentagesPattern.matcher(it) } )
        val matchersWithPercentage = matchers.filter { it.value.find() }

        return matchersWithPercentage.mapNotNull { mapPercentageToAmountOfMoney(it.value, it.key, percentageSymbol) }
    }

    protected open fun mapPercentageToAmountOfMoney(matcherWithPercentage: Matcher, line: String,
                                                    percentageSymbol: String): AmountOfMoney? {

        val percentageString = matcherWithPercentage.group()
        val percentageNumberAsString = percentageString.replace(percentageSymbol, "").trim()

        try {
            val percentageNumberAsFloat = extractNumber(percentageNumberAsString)?.toFloat() ?: percentageNumberAsString.toFloat()

            if (percentageNumberAsFloat in 0f..100f) { // is really a percentage
                return AmountOfMoney(percentageNumberAsFloat.toDouble(), percentageSymbol, percentageString, line)
            }
        } catch (e: Exception) { log.warn("Could not map $percentageNumberAsString to Float", e) }

        return null
    }


    protected open fun extractNumber(numberString: String): Number? {
        try {
            return UserNumberFormat.parse(numberString)
        } catch (ignored: Exception) { }

        try {
            return NumberFormatWithDotAsDecimalSeparator.parse(numberString)
        } catch (ignored: Exception) { }

        try {
            return NumberFormatWithCommaAsDecimalSeparator.parse(numberString)
        } catch (ignored: Exception) { }

        return null
    }


    protected open fun createCurrencySymbolPattern(currencySymbolPatternString: String): Pattern {
        return Pattern.compile(currencySymbolPatternString, Pattern.CASE_INSENSITIVE)
    }

    protected open fun createPatternForDecimalNumberBeforeCurrencySymbol(decimalNumberPatternString: String,
                                                                         currencySymbol: String): Pattern {

        return Pattern.compile(decimalNumberPatternString + "\\s*" + currencySymbol, Pattern.CASE_INSENSITIVE)
    }

    protected open fun createPatternForDecimalNumberAfterCurrencySymbol(decimalNumberPatternString: String,
                                                                         currencySymbol: String): Pattern {

        return Pattern.compile(currencySymbol + "\\s*" + decimalNumberPatternString, Pattern.CASE_INSENSITIVE)
    }

    protected open fun getPercentageSymbol(): String {
        return "%"
    }

    protected open fun createPatternForPercentage(decimalNumberPatternString: String,
                                                  percentageSymbol: String): Pattern {

        return Pattern.compile(decimalNumberPatternString + "\\s*" + percentageSymbol, Pattern.CASE_INSENSITIVE)
    }

}