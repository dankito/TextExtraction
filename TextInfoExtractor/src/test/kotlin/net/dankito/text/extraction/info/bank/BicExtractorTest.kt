package net.dankito.text.extraction.info.bank

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


class BicExtractorTest {

    private val underTest = BicExtractor()


    @Test
    fun `BIC without branch code`() {

        // when
        val result = underTest.extractBics("DEUTDEBB")

        // then
        assertThat(result).hasSize(1)
        assertThat(result.map { it.hit }).containsExactly("DEUTDEBB")
    }

    @Test
    fun `BIC with branch code`() {

        // when
        val result = underTest.extractBics("DEUTDEBBXXX")

        // then
        assertThat(result).hasSize(1)
        assertThat(result.map { it.hit }).containsExactly("DEUTDEBBXXX")
    }


    @Test
    fun `Not a BIC - lower case letter in 1st place`() {

        // when
        val result = underTest.extractBics("dEUTDEBB")

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `Not a BIC - lower case letter in 2nd place`() {

        // when
        val result = underTest.extractBics("DeUTDEBB")

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `Not a BIC - lower case letter in 3rd place`() {

        // when
        val result = underTest.extractBics("DEuTDEBB")

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `Not a BIC - lower case letter in 4th place`() {

        // when
        val result = underTest.extractBics("DEUtDEBB")

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `Not a BIC - lower case letter in 5th place`() {

        // when
        val result = underTest.extractBics("DEUTdEBB")

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `Not a BIC - lower case letter in 6th place`() {

        // when
        val result = underTest.extractBics("DEUTDeBB")

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `Not a BIC - lower case letter in 7th place`() {

        // when
        val result = underTest.extractBics("DEUTDEbB")

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `Not a BIC - lower case letter in 8th place`() {

        // when
        val result = underTest.extractBics("DEUTDEBb")

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `Not a BIC - lower case letter in 9th place`() {

        // when
        val result = underTest.extractBics("DEUTDEBBxXX")

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `Not a BIC - lower case letter in 10th place`() {

        // when
        val result = underTest.extractBics("DEUTDEBBXxX")

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `Not a BIC - lower case letter in 11th place`() {

        // when
        val result = underTest.extractBics("DEUTDEBBXXx")

        // then
        assertThat(result).isEmpty()
    }


    @Test
    fun `Not a BIC - not a letter in 1st place`() {

        // when
        val result = underTest.extractBics("1EUTDEBB")

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `Not a BIC - not a letter in 2nd place`() {

        // when
        val result = underTest.extractBics("D2UTDEBB")

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `Not a BIC - not a letter in 3rd place`() {

        // when
        val result = underTest.extractBics("DE3TDEBB")

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `Not a BIC - not a letter in 4th place`() {

        // when
        val result = underTest.extractBics("DEU4DEBB")

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `Not a BIC - not a letter in 5th place`() {

        // when
        val result = underTest.extractBics("DEUT5EBB")

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `Not a BIC - not a letter in 6th place`() {

        // when
        val result = underTest.extractBics("DEUTD6BB")

        // then
        assertThat(result).isEmpty()
    }

}