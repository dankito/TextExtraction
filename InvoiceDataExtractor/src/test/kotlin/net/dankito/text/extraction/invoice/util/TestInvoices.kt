package net.dankito.text.extraction.invoice.util

class TestInvoices {

    companion object {

        const val GermanWebHostingInvoice =
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


        const val GermanMobilePhoneInvoice =
                        "Ihre  Rechnung \n" +
                        "  \n" +
                        "  \n" +
                        "Abrechnungszeitpunkt:  20.03.2019 \n" +
                        "  \n" +
                        "  \n" +
                        "  \n" +
                        "  \n" +
                        "Entsprechend  Ihrem  SEPA-Lastschriftmandat  buchen  wir  den  Rechnungsbetrag  von  6,99  EUR  am  26.03.2019  von  Ihrem  Konto  ab. \n" +
                        "  \n" +
                        "Die  Daten  Ihres  SEPA-Lastschriftmandats  lauten: \n" +
                        "  \n" +
                        "Aus  technischen  Gründen  kann  es  vorkommen,  dass  der  oben  genannte  Betrag  um  mehrere  Tage  verzögert  abgebucht  wird. \n" +
                        "  \n" +
                        "Vertragslaufzeit \n" +
                        "Dieser  Vertrag  begann  am  20.12.2018  und  endet  am  19.12.2019.  Er  verlängert  sich  um  12  Monate,  wenn  er  nicht  fristgerecht  gekündigt  wird.  Die  Kündigungsfrist \n" +
                        "beträgt  3  Monate.  Das  letztmögliche  Kündigungsdatum  ist  also  am  19.09.2019. \n" +
                        "  \n" +
                        "  \n" +
                        "Pos. Die  Leistungen  im  ÜberblickTarifEinheitenBrutto  (EUR)MwSt \n" +
                        "Basispreis \n" +
                        "1 Grundgebühr  6,99  EUR  je  Monat1  Mon.6,9919% \n" +
                        "20.03.2019-20.04.2019 \n" +
                        "Zwischensumme  (Netto)5,87  EUR \n" +
                        "+  Mehrwertsteuer  (19,00%)1,12  EUR \n" +
                        "Zu  zahlender  Betrag6,99  EUR \n" +
                        "Rechnungsdatum 21.03.2019 \n" +
                        "Rechnungsnummer 150524767352 \n"
    }

}