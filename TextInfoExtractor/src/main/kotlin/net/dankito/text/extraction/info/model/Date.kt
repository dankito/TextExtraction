package net.dankito.text.extraction.info.model

import java.text.SimpleDateFormat
import java.util.Date


open class Date(val day: Int, val month: Int, val year: Int, val dateString: String, foundInLine: String) :
    SearchResult(foundInLine) {

    companion object {
        val DateFormatter = SimpleDateFormat("dd.MM.yyyy")
    }


    open fun toJavaUtilDate(): Date {
        return DateFormatter.parse(getDateAccordingToDateFormat())
    }

    protected open fun getDateAccordingToDateFormat() = "$day.$month.$year"



    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is net.dankito.text.extraction.info.model.Date) return false

        if (day != other.day) return false
        if (month != other.month) return false
        if (year != other.year) return false

        return true
    }

    override fun hashCode(): Int {
        var result = day
        result = 31 * result + month
        result = 31 * result + year
        return result
    }


    override fun toString(): String {
        return getDateAccordingToDateFormat()
    }

}