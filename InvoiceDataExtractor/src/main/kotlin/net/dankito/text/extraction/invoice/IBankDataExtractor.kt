package net.dankito.text.extraction.invoice

import net.dankito.text.extraction.invoice.model.BankData


interface IBankDataExtractor {

    fun extractBankData(text: String): BankData

    fun extractBankData(lines: List<String>): BankData

}