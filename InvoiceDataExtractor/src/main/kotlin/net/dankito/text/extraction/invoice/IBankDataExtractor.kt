package net.dankito.text.extraction.invoice

import net.dankito.text.extraction.invoice.model.BankData
import net.dankito.text.extraction.invoice.model.StringSearchResult


interface IBankDataExtractor {

    fun extractBankData(text: String): BankData

    fun extractBankData(lines: List<String>): BankData


    fun extractIbans(text: String): List<StringSearchResult>

    fun extractIbans(lines: List<String>): List<StringSearchResult>


    fun extractBics(text: String): List<StringSearchResult>

    fun extractBics(lines: List<String>): List<StringSearchResult>

}