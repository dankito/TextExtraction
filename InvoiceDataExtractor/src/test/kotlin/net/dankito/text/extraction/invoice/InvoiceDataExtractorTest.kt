package net.dankito.text.extraction.invoice

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


class InvoiceDataExtractorTest {

    private val underTest = InvoiceDataExtractor()


    @Test
    fun extractGermanWebHostingInvoiceData() {

        // when
        val result = underTest.extractInvoiceData(germanWebHostingInvoice)


        // then
        assertThat(result.totalAmount.amount).isEqualTo(15.0)
        assertThat(result.totalAmount.currency).isEqualTo("€")

        assertThat(result.netAmout?.amount).isEqualTo(12.61)
        assertThat(result.netAmout?.currency).isEqualTo("€")

        assertThat(result.valueAddedTax?.amount).isEqualTo(2.39)
        assertThat(result.valueAddedTax?.currency).isEqualTo("€")

        assertThat(result.valueAddedTaxRate).isEqualTo(19f)
    }


    private val germanWebHostingInvoice =
                "Rechnung  Nr.  1201440 \n" +
                "Datum: \n" +
                "Zahlungsweise: \n" +
                "Kunden-Nummer: 02.05.2019 \n" +
                "SEPA-Lastschrift \n" +
                "13570 \n" +
                "Pos. Menge Beschreibung MwSt.-Satz Einzelpreis Gesamtpreis \n" +
                "1 6 DE 19  %2,10€12,61€Paket  Webhosting  M \n" +
                "Summe  netto12,61€ \n" +
                "zzgl.  MwSt.  (DE  19  %)2,39€ \n" +
                "Summe  MwSt.2,39€ \n" +
                "Summe  brutto15,00€ \n" +
                "Der  Rechnungsbetrag  wird  am  13.05.2019  per  SEPA-Lastschrift  von Ihrem  Konto abgebucht. \n" +
                "Sofern  nichts  Anderes  angegeben  ist,  entspricht  das  Liefer-  bzw  Leistungsdatum  dem  Rechnungsdatum. \n" +
                "Es  können  Rundungsdifferenzen  zwischen  Netto-  und  Brutto-Preisen  sowie  Summen  entstehen. "

}