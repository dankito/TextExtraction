package net.dankito.text.extraction.invoice

import net.dankito.text.extraction.invoice.model.AmountOfMoney
import net.dankito.text.extraction.invoice.model.TotalNetAndVatAmount


interface IAmountCategorizer {

    fun findTotalNetAndVatAmount(amounts: Collection<AmountOfMoney>): TotalNetAndVatAmount?

}