package net.dankito.text.extraction.invoice

import net.dankito.text.extraction.invoice.model.Date


interface IDateExtractor {

    fun extractDates(text: String): List<Date>

    fun extractDates(lines: List<String>): List<Date>

}