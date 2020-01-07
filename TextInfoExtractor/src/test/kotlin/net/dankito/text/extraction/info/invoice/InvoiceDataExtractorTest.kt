package net.dankito.text.extraction.info.invoice

import net.dankito.text.extraction.info.util.TestInvoices
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert
import org.junit.Test


class InvoiceDataExtractorTest {

    private val underTest = InvoiceDataExtractor()


    @Test
    fun extractInvoiceData_GermanWebHostingInvoice() {

        // when
        val result = underTest.extractInvoiceData(TestInvoices.GermanWebHostingInvoice)


        // then
        assertThat(result.error).isNull()

        assertThat(result.allAmounts).extracting("amount").containsOnly(2.1, 12.61, 2.39, 15.0)

        assertThat(result.dates).extracting("dateString").containsOnly("02.05.2019", "13.05.2019")

        assertThat(result.ibans).isEmpty()

        assertThat(result.bics).isEmpty()

        assertThat(result.potentialTotalAmount?.amount).isEqualTo(15.0)
        assertThat(result.potentialTotalAmount?.currency).isEqualTo("€")

        assertThat(result.potentialNetAmount?.amount).isEqualTo(12.61)
        assertThat(result.potentialNetAmount?.currency).isEqualTo("€")

        assertThat(result.potentialValueAddedTax?.amount).isEqualTo(2.39)
        assertThat(result.potentialValueAddedTax?.currency).isEqualTo("€")

        assertThat(result.potentialValueAddedTaxRate?.amount).isEqualTo(19.0)
        assertThat(result.potentialValueAddedTaxRate?.currency).isEqualTo("%")
    }

    @Test
    fun extractInvoiceData_GermanMobilePhoneInvoice_CurrencySymbolEUR() {

        // when
        val result = underTest.extractInvoiceData(TestInvoices.GermanMobilePhoneInvoice)


        // then
        assertThat(result.error).isNull()

        assertThat(result.allAmounts).extracting("amount").containsOnly(6.99, 1.12, 5.87)

        assertThat(result.dates).extracting("dateString").containsOnly("20.03.2019", "26.03.2019", "20.12.2018", "19.12.2019", "19.09.2019", "20.04.2019", "21.03.2019")

        assertThat(result.ibans).isEmpty()

        assertThat(result.bics).isEmpty()

        assertThat(result.potentialTotalAmount?.amount).isEqualTo(6.99)
        assertThat(result.potentialTotalAmount?.currency).isEqualTo("EUR")

        assertThat(result.potentialNetAmount?.amount).isEqualTo(5.87)
        assertThat(result.potentialNetAmount?.currency).isEqualTo("EUR")

        assertThat(result.potentialValueAddedTax?.amount).isEqualTo(1.12)
        assertThat(result.potentialValueAddedTax?.currency).isEqualTo("EUR")

        assertThat(result.potentialValueAddedTaxRate?.amount).isEqualTo(19.0)
        assertThat(result.potentialValueAddedTaxRate?.currency).isEqualTo("%")
    }

    @Test
    fun extractInvoiceData_GermanBackendDevelopmentInvoice() {

        // given
        val invoiceText = readFileFromResource("Invoice Backend Development German.txt")

        // when
        val result = underTest.extractInvoiceData(invoiceText)


        // then
        assertThat(result.error).isNull()

        assertThat(result.allAmounts).extracting("amount").containsOnly(80.0, 10903.2, 2071.0, 12974.2)

        assertThat(result.dates).extracting("dateString").containsOnly("31.01.2020")

        assertThat(result.ibans).extracting("hit").containsOnly("DE11876543211234567890")

        assertThat(result.bics).extracting("hit").containsOnly("ABCDDEBBXXX")

        assertThat(result.potentialTotalAmount?.amount).isEqualTo(12974.20)
        assertThat(result.potentialTotalAmount?.currency).isEqualTo("€")

        assertThat(result.potentialNetAmount?.amount).isEqualTo(10903.20)
        assertThat(result.potentialNetAmount?.currency).isEqualTo("€")

        assertThat(result.potentialValueAddedTax?.amount).isEqualTo(2071.00)
        assertThat(result.potentialValueAddedTax?.currency).isEqualTo("€")

        assertThat(result.potentialValueAddedTaxRate?.amount).isEqualTo(19.0)
        assertThat(result.potentialValueAddedTaxRate?.currency).isEqualTo("%")
    }


    private fun readFileFromResource(filename: String): String {
        val inputStream = InvoiceDataExtractorTest::class.java.classLoader.getResourceAsStream("test_data/" + filename)

        if (inputStream == null) {
            Assert.fail("Could not find file test_data/$filename in resources folder")
        }

        return inputStream.bufferedReader().readText()
    }

}