package net.dankito.text.extraction.invoice

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


class BankDataExtractorTest {

    private val underTest = BankDataExtractor()


    @Test
    fun extractBankData() {

        // when
        val result = underTest.extractBankData(
            "Unsere Bankverbindung:\n" +
                    "ABC GmbH<br/>\n" +
                    "Deutsche Bank<br/>\n" +
                    "BLZ: 59070070<br/>\n" +
                    "Konto-Nr.: 017905123<br/>\n" +
                    "BIC: DEUTDEDB595<br/>\n" +
                    "IBAN: DE06590700700017905123<br/>"
        )

        assertThat(result.ibans).hasSize(1)
        assertThat(result.ibans).extracting("hit").containsExactly("DE06590700700017905123")
        assertThat(result.ibans).extracting("foundInLine").containsExactly("IBAN: DE06590700700017905123<br/>")

        assertThat(result.bics).hasSize(1)
        assertThat(result.bics).extracting("hit").containsExactly("DEUTDEDB595")
        assertThat(result.bics).extracting("foundInLine").containsExactly("BIC: DEUTDEDB595<br/>")
    }

    @Test
    fun extractBankData2() {

        // when
        val result = underTest.extractBankData(
            "Please transfer the order amount to this bank account:\n" +
                    "Accountholder: abc Company\n" +
                    "Accountnumber: 4037290777\n" +
                    "Bank code: 43060967\n" +
                    "IBAN: DE47430609674037290777\n" +
                    "BIC: GENODEM1GLS\n" +
                    "Bank: GLS Gemeinschaftsbank eG"
        )

        assertThat(result.ibans).hasSize(1)
        assertThat(result.ibans).extracting("hit").containsExactlyInAnyOrder("DE47430609674037290777")
        assertThat(result.ibans).extracting("foundInLine").containsExactlyInAnyOrder("IBAN: DE47430609674037290777")

        assertThat(result.bics).hasSize(1)
        assertThat(result.bics).extracting("hit").containsExactlyInAnyOrder("GENODEM1GLS")
        assertThat(result.bics).extracting("foundInLine").containsExactlyInAnyOrder("BIC: GENODEM1GLS")
    }

    @Test
    fun extractBankData_IbanWithSpaces() {

        // when
        val result = underTest.extractBankData(
            "Bankverbindung:\n" +
                    "Postbank Hannover\n" +
                    "Konto: \t540 160 404\n" +
                    "BLZ: \t250 100 30\n" +
                    "IBAN: \tDE22 2501 0030 0540 1604 04\n" +
                    "BIC: \tPBNKDEFF250\n" +
                    "Umsatzsteuer-ID-Nr.: \tDE 813 501 978"
        )

        assertThat(result.ibans).hasSize(1)
        assertThat(result.ibans).extracting("hit").containsExactly("DE22 2501 0030 0540 1604 04")
        assertThat(result.ibans).extracting("foundInLine").containsExactly("IBAN: \tDE22 2501 0030 0540 1604 04")

        assertThat(result.bics).hasSize(1)
        assertThat(result.bics).extracting("hit").containsExactly("PBNKDEFF250")
        assertThat(result.bics).extracting("foundInLine").containsExactly("BIC: \tPBNKDEFF250")
    }

    @Test
    fun extractBankData_IbanWithSpaces2() {

        // when
        val result = underTest.extractBankData(
            "abc GmbH & Co. KG\n" +
                    "Commerzbank\n" +
                    "IBAN: DE15 3904 0013 0122 3080 99\n" +
                    "SWIFT/BIC: COBADEFF390"
        )

        assertThat(result.ibans).hasSize(1)
        assertThat(result.ibans).extracting("hit").containsExactlyInAnyOrder("DE15 3904 0013 0122 3080 99")
        assertThat(result.ibans).extracting("foundInLine")
            .containsExactlyInAnyOrder("IBAN: DE15 3904 0013 0122 3080 99")

        assertThat(result.bics).hasSize(1)
        assertThat(result.bics).extracting("hit").containsExactlyInAnyOrder("COBADEFF390")
        assertThat(result.bics).extracting("foundInLine").containsExactlyInAnyOrder("SWIFT/BIC: COBADEFF390")
    }

    @Test
    fun extractBankData_MultipleIbans() {

        // when
        val result = underTest.extractBankData(
            "Postbank AG\n" +
                    "BIC PBNKDEFFXXX • IBAN DE50 7001 0080 0014 0608 77\n" +
                    "UniCredit Bank (HVB)\n" +
                    "BIC HYVEDEMMXXX • IBAN DE64 7002 0270 0000 0888 33\n" +
                    "Stadtsparkasse München\n" +
                    "BIC SSKMDEMMXXX • IBAN DE23 7015 0000 0000 1098 07\n"
        )

        assertThat(result.ibans).hasSize(3)
        assertThat(result.ibans).extracting("hit").containsExactlyInAnyOrder(
            "DE50 7001 0080 0014 0608 77",
            "DE64 7002 0270 0000 0888 33",
            "DE23 7015 0000 0000 1098 07"
        )
        assertThat(result.ibans).extracting("foundInLine").containsExactlyInAnyOrder(
            "BIC PBNKDEFFXXX • IBAN DE50 7001 0080 0014 0608 77",
            "BIC HYVEDEMMXXX • IBAN DE64 7002 0270 0000 0888 33",
            "BIC SSKMDEMMXXX • IBAN DE23 7015 0000 0000 1098 07"
        )

        assertThat(result.bics).hasSize(3)
        assertThat(result.bics).extracting("hit").containsExactlyInAnyOrder(
            "PBNKDEFFXXX",
            "HYVEDEMMXXX",
            "SSKMDEMMXXX"
        )
        assertThat(result.bics).extracting("foundInLine").containsExactlyInAnyOrder(
            "BIC PBNKDEFFXXX • IBAN DE50 7001 0080 0014 0608 77",
            "BIC HYVEDEMMXXX • IBAN DE64 7002 0270 0000 0888 33",
            "BIC SSKMDEMMXXX • IBAN DE23 7015 0000 0000 1098 07"
        )
    }

}