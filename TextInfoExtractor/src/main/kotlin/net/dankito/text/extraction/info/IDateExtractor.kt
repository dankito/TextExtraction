package net.dankito.text.extraction.info

import net.dankito.text.extraction.info.model.DateData


interface IDateExtractor {

    fun extractDates(text: String): List<DateData>

    fun extractDates(lines: List<String>): List<DateData>

}