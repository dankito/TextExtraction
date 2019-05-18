package net.dankito.text.extraction.invoice

import net.dankito.text.extraction.invoice.model.AmountOfMoney
import net.dankito.text.extraction.invoice.model.Invoice


open class InvoiceDataExtractor @JvmOverloads constructor(protected val amountExtractor: IAmountExtractor = AmountExtractor()) {


    open fun extractInvoiceData(text: String): Invoice? {
        return extractInvoiceData(text.split("\n"))
    }

    open fun extractInvoiceData(lines: List<String>): Invoice? {

        val amounts = amountExtractor.extractAmountsOfMoney(lines)

        val vatRateCandidates = amountExtractor.extractPercentages(lines)

        findTotalNetAndVatAmount(amounts)?.let { potentialAmounts ->
            return Invoice(potentialAmounts.first, potentialAmounts.second, potentialAmounts.third)
        }

        return null
    }


    protected open fun findTotalNetAndVatAmount(amounts: Collection<AmountOfMoney>)
            : Triple<AmountOfMoney, AmountOfMoney?, AmountOfMoney?>? {

        if (amounts.isNotEmpty()) {
            val amountsSorted = amounts.sortedByDescending { it.amount }

            for (totalIndex in 0 until amountsSorted.size) {
                val potentialTotal = amountsSorted[totalIndex]

                for (netIndex in (totalIndex + 1) until amountsSorted.size - 1) {
                    val potentialNet = amountsSorted[netIndex]

                    for (vatIndex in (netIndex + 1) until amountsSorted.size) {
                        val potentialVat = amountsSorted[vatIndex]

                        if (potentialTotal.amount == (potentialNet.amount + potentialVat.amount)) {
                            return Triple(potentialTotal, potentialNet, potentialVat)
                        }
                    }
                }
            }

            return Triple(amountsSorted.first(), null, null)
        }

        return null
    }

}