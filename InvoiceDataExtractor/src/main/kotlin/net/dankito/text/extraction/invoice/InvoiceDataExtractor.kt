package net.dankito.text.extraction.invoice

import net.dankito.text.extraction.invoice.model.Invoice


open class InvoiceDataExtractor @JvmOverloads constructor(
    protected val amountExtractor: IAmountExtractor = AmountExtractor(),
    protected val amountCategorizer: AmountCategorizer = AmountCategorizer()
) {


    open fun extractInvoiceData(text: String): Invoice? {
        return extractInvoiceData(text.split("\n"))
    }

    open fun extractInvoiceData(lines: List<String>): Invoice? {

        val amounts = amountExtractor.extractAmountsOfMoney(lines)

        val vatRateCandidates = amountExtractor.extractPercentages(lines)

        amountCategorizer.findTotalNetAndVatAmount(amounts)?.let { potentialAmounts ->
            return Invoice(potentialAmounts.first, potentialAmounts.second, potentialAmounts.third)
        }

        return null
    }

}