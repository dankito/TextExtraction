package net.dankito.text.extraction.invoice

import net.dankito.text.extraction.invoice.model.AmountOfMoney


open class AmountCategorizer : IAmountCategorizer {

    override fun findTotalNetAndVatAmount(amounts: Collection<AmountOfMoney>)
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