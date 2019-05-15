package net.dankito.text.extraction.invoice

import net.dankito.text.extraction.invoice.util.TestInvoices
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


class InvoiceDataExtractorTest {

    private val underTest = InvoiceDataExtractor()


    @Test
    fun extractGermanWebHostingInvoiceData() {

        // when
        val result = underTest.extractInvoiceData(TestInvoices.GermanWebHostingInvoice)


        // then
        assertThat(result.totalAmount.amount).isEqualTo(15.0)
        assertThat(result.totalAmount.currency).isEqualTo("€")

        assertThat(result.netAmout?.amount).isEqualTo(12.61)
        assertThat(result.netAmout?.currency).isEqualTo("€")

        assertThat(result.valueAddedTax?.amount).isEqualTo(2.39)
        assertThat(result.valueAddedTax?.currency).isEqualTo("€")

        assertThat(result.valueAddedTaxRate).isEqualTo(19f)
    }

    @Test
    fun extractGermanMobilePhoneInvoiceData_CurrencySymbolEUR() {

        // when
        val result = underTest.extractInvoiceData(TestInvoices.GermanMobilePhoneInvoice)


        // then
        assertThat(result.totalAmount.amount).isEqualTo(6.99)
        assertThat(result.totalAmount.currency).isEqualTo("€")

        assertThat(result.netAmout?.amount).isEqualTo(5.87)
        assertThat(result.netAmout?.currency).isEqualTo("€")

        assertThat(result.valueAddedTax?.amount).isEqualTo(1.12)
        assertThat(result.valueAddedTax?.currency).isEqualTo("€")

        assertThat(result.valueAddedTaxRate).isEqualTo(19f)
    }

}