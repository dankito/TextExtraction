package net.dankito.text.extraction.info

import net.dankito.text.extraction.info.model.AmountOfMoney
import net.dankito.text.extraction.info.model.TotalNetAndVatAmount


interface IAmountCategorizer {

    fun findTotalNetAndVatAmount(amounts: Collection<AmountOfMoney>): TotalNetAndVatAmount?

    fun findValueAddedTaxRate(percentages: List<AmountOfMoney>): AmountOfMoney?

}