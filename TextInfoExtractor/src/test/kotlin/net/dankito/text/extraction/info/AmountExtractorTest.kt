package net.dankito.text.extraction.info

import net.dankito.text.extraction.info.model.AmountOfMoney
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource


class AmountExtractorTest {

    companion object {
        private val CurrencySymbols = listOf("$", "€", "EUR", "£") // cannot be made a compile-time constant in Kotlin so that @ValueSource does not work

        @JvmStatic // must be static for JUnit / JVM
        fun currenciesProvider() = CurrencySymbols // therefore we supply a method as provider
    }


    private val underTest = AmountExtractor()


    @ParameterizedTest
    @MethodSource("currenciesProvider")
    fun `Integer`(currency: String) {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10 $currency"))

        // then
        assertAmountAndCurrency(result, 10.0, currency)
    }


    @ParameterizedTest
    @MethodSource("currenciesProvider")
    fun `Dot as decimal separator`(currency: String) {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10.51 $currency"))

        // then
        assertAmountAndCurrency(result, 10.51, currency)
    }

    @ParameterizedTest
    @MethodSource("currenciesProvider")
    fun `Comma as decimal separator`(currency: String) {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10,51 $currency"))

        // then
        assertAmountAndCurrency(result, 10.51, currency)
    }


    @ParameterizedTest
    @MethodSource("currenciesProvider")
    fun `Comma as thousand separator`(currency: String) {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1,234,567 $currency"))

        // then
        assertAmountAndCurrency(result, 1234567.0, currency)
    }

    @ParameterizedTest
    @MethodSource("currenciesProvider")
    fun `Dot as thousand separator`(currency: String) {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1.234.567 $currency"))

        // then
        assertAmountAndCurrency(result, 1234567.0, currency)
    }


    @ParameterizedTest
    @MethodSource("currenciesProvider")
    fun `Comma as thousand separator and dot as decimal separator`(currency: String) {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1,234,567.89 $currency"))

        // then
        assertAmountAndCurrency(result, 1234567.89, currency)
    }

    @ParameterizedTest
    @MethodSource("currenciesProvider")
    fun `Dot as thousand separator and comma as decimal separator`(currency: String) {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1.234.567,89 $currency"))

        // then
        assertAmountAndCurrency(result, 1234567.89, currency)
    }


    private fun assertAmountAndCurrency(result: List<AmountOfMoney>, amount: Double, currency: String) {
        assertThat(result).extracting("amount").containsExactly(amount)

        assertThat(result).extracting("currency").containsExactly(currency)
    }

}