package net.dankito.text.extraction.info

import net.dankito.text.extraction.info.model.Date


interface IDateExtractor {

    fun extractDates(text: String): List<Date>

    fun extractDates(lines: List<String>): List<Date>

}