package net.dankito.text.extraction.info

import net.dankito.text.extraction.info.model.AmountOfMoney
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


class AmountExtractorTest {

    private val underTest = AmountExtractor()


    @Test
    fun `Integer € symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10 €"))

        // then
        assertAmountAndCurrency(result, 10.0, "€")
    }

    @Test
    fun `Integer EUR symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10 EUR"))

        // then
        assertAmountAndCurrency(result, 10.0, "EUR")
    }

    @Test
    fun `Integer $ symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10 $"))

        // then
        assertAmountAndCurrency(result, 10.0, "$")
    }

    @Test
    fun `Integer £ symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10 £"))

        // then
        assertAmountAndCurrency(result, 10.0, "£")
    }


    @Test
    fun `Dot as decimal separator € symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10.51 €"))

        // then
        assertAmountAndCurrency(result, 10.51, "€")
    }

    @Test
    fun `Dot as decimal separator EUR symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10.51 EUR"))

        // then
        assertAmountAndCurrency(result, 10.51, "EUR")
    }

    @Test
    fun `Dot as decimal separator $ symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10.51 $"))

        // then
        assertAmountAndCurrency(result, 10.51, "$")
    }

    @Test
    fun `Dot as decimal separator £ symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10.51 £"))

        // then
        assertAmountAndCurrency(result, 10.51, "£")
    }


    @Test
    fun `Comma as decimal separator € symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10,51 €"))

        // then
        assertAmountAndCurrency(result, 10.51, "€")
    }

    @Test
    fun `Comma as decimal separator EUR symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10,51 EUR"))

        // then
        assertAmountAndCurrency(result, 10.51, "EUR")
    }

    @Test
    fun `Comma as decimal separator $ symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10,51 $"))

        // then
        assertAmountAndCurrency(result, 10.51, "$")
    }

    @Test
    fun `Comma as decimal separator £ symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10,51 £"))

        // then
        assertAmountAndCurrency(result, 10.51, "£")
    }


    @Test
    fun `Comma as thousand separator € symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1,234,567 €"))

        // then
        assertAmountAndCurrency(result, 1234567.0, "€")
    }

    @Test
    fun `Comma as thousand separator EUR symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1,234,567 EUR"))

        // then
        assertAmountAndCurrency(result, 1234567.0, "EUR")
    }

    @Test
    fun `Comma as thousand separator $ symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1,234,567 $"))

        // then
        assertAmountAndCurrency(result, 1234567.0, "$")
    }

    @Test
    fun `Comma as thousand separator £ symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1,234,567 £"))

        // then
        assertAmountAndCurrency(result, 1234567.0, "£")
    }


    @Test
    fun `Dot as thousand separator € symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1.234.567 €"))

        // then
        assertAmountAndCurrency(result, 1234567.0, "€")
    }

    @Test
    fun `Dot as thousand separator EUR symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1.234.567 EUR"))

        // then
        assertAmountAndCurrency(result, 1234567.0, "EUR")
    }

    @Test
    fun `Dot as thousand separator $ symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1.234.567 $"))

        // then
        assertAmountAndCurrency(result, 1234567.0, "$")
    }

    @Test
    fun `Dot as thousand separator £ symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1.234.567 £"))

        // then
        assertAmountAndCurrency(result, 1234567.0, "£")
    }


    @Test
    fun `Comma as thousand separator and dot as decimal separator € symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1,234,567.89 €"))

        // then
        assertAmountAndCurrency(result, 1234567.89, "€")
    }

    @Test
    fun `Comma as thousand separator and dot as decimal separator EUR symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1,234,567.89 EUR"))

        // then
        assertAmountAndCurrency(result, 1234567.89, "EUR")
    }

    @Test
    fun `Comma as thousand separator and dot as decimal separator $ symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1,234,567.89 $"))

        // then
        assertAmountAndCurrency(result, 1234567.89, "$")
    }

    @Test
    fun `Comma as thousand separator and dot as decimal separator £ symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1,234,567.89 £"))

        // then
        assertAmountAndCurrency(result, 1234567.89, "£")
    }


    @Test
    fun `Dot as thousand separator and comma as decimal separator € symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1.234.567,89 €"))

        // then
        assertAmountAndCurrency(result, 1234567.89, "€")
    }

    @Test
    fun `Dot as thousand separator and comma as decimal separator EUR symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1.234.567,89 EUR"))

        // then
        assertAmountAndCurrency(result, 1234567.89, "EUR")
    }

    @Test
    fun `Dot as thousand separator and comma as decimal separator $ symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1.234.567,89 $"))

        // then
        assertAmountAndCurrency(result, 1234567.89, "$")
    }

    @Test
    fun `Dot as thousand separator and comma as decimal separator £ symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("1.234.567,89 £"))

        // then
        assertAmountAndCurrency(result, 1234567.89, "£")
    }


    private fun assertAmountAndCurrency(result: List<AmountOfMoney>, amount: Double, currency: String) {
        assertThat(result).extracting("amount").containsExactly(amount)

        assertThat(result).extracting("currency").containsExactly(currency)
    }

}