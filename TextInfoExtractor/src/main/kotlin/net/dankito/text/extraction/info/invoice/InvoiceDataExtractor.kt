package net.dankito.text.extraction.info.invoice

import net.dankito.text.extraction.info.*
import net.dankito.text.extraction.info.model.InvoiceData
import org.slf4j.LoggerFactory


open class InvoiceDataExtractor @JvmOverloads constructor(
    protected val amountExtractor: IAmountExtractor = AmountExtractor(),
    protected val amountCategorizer: IAmountCategorizer = AmountCategorizer(),
    protected val dateExtractor: DateExtractor = DateExtractor()
) : ExtractorBase() {

    companion object {
        private val log = LoggerFactory.getLogger(InvoiceDataExtractor::class.java)
    }


    open fun extractInvoiceData(text: String): InvoiceData {
        return extractInvoiceData(getLines(text))
    }

    open fun extractInvoiceData(lines: List<String>): InvoiceData {
        try {
            val percentages = amountExtractor.extractPercentages(lines)

            val potentialVatRate = amountCategorizer.findValueAddedTaxRate(percentages)

            val amounts = amountExtractor.extractAmountsOfMoney(lines)

            val dates = dateExtractor.extractDates(lines)

            amountCategorizer.findTotalNetAndVatAmount(amounts)?.let { potentialAmounts ->
                return InvoiceData(potentialAmounts.totalAmount, potentialAmounts.netAmount, potentialAmounts.valueAddedTax, potentialVatRate)
            }

            return InvoiceData.couldNotExtractInvoiceData(null) // will be fixed soon
        } catch (e: Exception) {
            log.error("Could not extract invoice data from:${lines.map { System.lineSeparator() + it }}", e)

            return InvoiceData.couldNotExtractInvoiceData(e)
        }
    }

}