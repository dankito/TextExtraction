package net.dankito.text.extraction.invoice

import net.dankito.text.extraction.invoice.model.AmountOfMoney


interface IAmountExtractor {

    open fun extractAmountsOfMoney(lines: List<String>): List<AmountOfMoney>

    open fun extractPercentages(lines: List<String>): List<AmountOfMoney>

}