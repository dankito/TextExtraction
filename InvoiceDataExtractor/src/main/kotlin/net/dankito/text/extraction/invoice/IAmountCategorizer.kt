package net.dankito.text.extraction.invoice

import net.dankito.text.extraction.invoice.model.AmountOfMoney


interface IAmountCategorizer {

    fun findTotalNetAndVatAmount(amounts: Collection<AmountOfMoney>)
            : Triple<AmountOfMoney, AmountOfMoney?, AmountOfMoney?>?

}