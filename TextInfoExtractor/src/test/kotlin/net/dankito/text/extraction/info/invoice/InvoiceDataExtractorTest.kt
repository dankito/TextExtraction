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

        assertThat(result.totalAmount.amount).isEqualTo(15.0)
        assertThat(result.totalAmount.currency).isEqualTo("€")

        assertThat(result.netAmount?.amount).isEqualTo(12.61)
        assertThat(result.netAmount?.currency).isEqualTo("€")

        assertThat(result.valueAddedTax?.amount).isEqualTo(2.39)
        assertThat(result.valueAddedTax?.currency).isEqualTo("€")

        assertThat(result.valueAddedTaxRate?.amount).isEqualTo(19.0)
        assertThat(result.valueAddedTaxRate?.currency).isEqualTo("%")
    }

    @Test
    fun extractInvoiceData_GermanMobilePhoneInvoice_CurrencySymbolEUR() {

        // when
        val result = underTest.extractInvoiceData(TestInvoices.GermanMobilePhoneInvoice)


        // then
        assertThat(result.error).isNull()

        assertThat(result.totalAmount.amount).isEqualTo(6.99)
        assertThat(result.totalAmount.currency).isEqualTo("EUR")

        assertThat(result.netAmount?.amount).isEqualTo(5.87)
        assertThat(result.netAmount?.currency).isEqualTo("EUR")

        assertThat(result.valueAddedTax?.amount).isEqualTo(1.12)
        assertThat(result.valueAddedTax?.currency).isEqualTo("EUR")

        assertThat(result.valueAddedTaxRate?.amount).isEqualTo(19.0)
        assertThat(result.valueAddedTaxRate?.currency).isEqualTo("%")
    }

    @Test
    fun extractInvoiceData_GermanBackendDevelopmentInvoice() {

        // given
        val invoiceText = readFileFromResource("Invoice Backend Development German.txt")

        // when
        val result = underTest.extractInvoiceData(invoiceText)


        // then
        assertThat(result.error).isNull()

        assertThat(result.totalAmount.amount).isEqualTo(12974.20)
        assertThat(result.totalAmount.currency).isEqualTo("€")

        assertThat(result.netAmount?.amount).isEqualTo(10903.20)
        assertThat(result.netAmount?.currency).isEqualTo("€")

        assertThat(result.valueAddedTax?.amount).isEqualTo(2071.00)
        assertThat(result.valueAddedTax?.currency).isEqualTo("€")

        assertThat(result.valueAddedTaxRate?.amount).isEqualTo(19.0)
        assertThat(result.valueAddedTaxRate?.currency).isEqualTo("%")
    }


    private fun readFileFromResource(filename: String): String {
        val inputStream = InvoiceDataExtractorTest::class.java.classLoader.getResourceAsStream("test_data/" + filename)

        if (inputStream == null) {
            Assert.fail("Could not find file test_data/$filename in resources folder")
        }

        return inputStream.bufferedReader().readText()
    }

}