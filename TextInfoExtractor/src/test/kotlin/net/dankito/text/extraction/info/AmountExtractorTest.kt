package net.dankito.text.extraction.info

import net.dankito.text.extraction.info.model.AmountOfMoney
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


class AmountExtractorTest {

    private val underTest = AmountExtractor()


    @Test
    fun `Decimal € symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10 €"))

        // then
        assertAmountAndCurrency(result, 10.0, "€")
    }

    @Test
    fun `Decimal EUR symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10 EUR"))

        // then
        assertAmountAndCurrency(result, 10.0, "EUR")
    }

    @Test
    fun `Decimal $ symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10 $"))

        // then
        assertAmountAndCurrency(result, 10.0, "$")
    }

    @Test
    fun `Decimal £ symbol`() {

        // when
        val result = underTest.extractAmountsOfMoney(listOf("10 £"))

        // then
        assertAmountAndCurrency(result, 10.0, "£")
    }


    private fun assertAmountAndCurrency(result: List<AmountOfMoney>, amount: Double, currency: String) {
        assertThat(result).extracting("amount").containsExactly(amount)

        assertThat(result).extracting("currency").containsExactly(currency)
    }

}