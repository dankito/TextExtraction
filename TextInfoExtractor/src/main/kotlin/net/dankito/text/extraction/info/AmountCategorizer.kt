package net.dankito.text.extraction.info

import net.dankito.text.extraction.info.model.AmountOfMoney
import net.dankito.text.extraction.info.model.TotalNetAndVatAmount


open class AmountCategorizer : IAmountCategorizer {

    companion object {

        // see https://en.wikipedia.org/wiki/List_of_countries_by_tax_rates; 18.05.2019: India
        const val HighestValueAddedTaxRate = 28.0

    }

    override fun findTotalNetAndVatAmount(amounts: Collection<AmountOfMoney>): TotalNetAndVatAmount? {

        if (amounts.isNotEmpty()) {
            val amountsSorted = amounts.sortedByDescending { it.amount }

            for (totalIndex in 0 until amountsSorted.size) {
                val potentialTotal = amountsSorted[totalIndex]

                for (netIndex in (totalIndex + 1) until amountsSorted.size - 1) {
                    val potentialNet = amountsSorted[netIndex]

                    for (vatIndex in (netIndex + 1) until amountsSorted.size) {
                        val potentialVat = amountsSorted[vatIndex]

                        if (potentialTotal.amount == (potentialNet.amount + potentialVat.amount)) {
                            return TotalNetAndVatAmount(potentialTotal, potentialNet, potentialVat)
                        }
                    }
                }
            }

            return TotalNetAndVatAmount(amountsSorted.first(), null, null)
        }

        return null
    }


    override fun findValueAddedTaxRate(percentages: List<AmountOfMoney>): AmountOfMoney? {
        if (percentages.isNotEmpty()) {
            if (percentages.size == 1) {
                return percentages.first()
            }

            val percentagesSorted = percentages.sortedByDescending { it.amount }

            val realisticTaxRates = percentagesSorted.filter { it.amount <= HighestValueAddedTaxRate }

            if (realisticTaxRates.isNotEmpty()) {
                return realisticTaxRates.first() // take the highest from all percentages lower than HighestValueAddedTaxRate
            }

            return percentagesSorted.last() // if there are no percentages lower than HighestValueAddedTaxRate, take lowest one
        }

        return null
    }

}