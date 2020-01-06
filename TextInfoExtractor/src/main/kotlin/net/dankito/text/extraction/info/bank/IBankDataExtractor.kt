package net.dankito.text.extraction.info.bank

import net.dankito.text.extraction.info.model.BankData


interface IBankDataExtractor {

    fun extractBankData(text: String): BankData

    fun extractBankData(lines: List<String>): BankData

}