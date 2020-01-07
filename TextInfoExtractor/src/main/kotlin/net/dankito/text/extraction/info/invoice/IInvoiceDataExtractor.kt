package net.dankito.text.extraction.info.invoice

import net.dankito.text.extraction.info.model.InvoiceData


interface IInvoiceDataExtractor {

    fun extractInvoiceData(text: String): InvoiceData

    fun extractInvoiceData(lines: List<String>): InvoiceData

}