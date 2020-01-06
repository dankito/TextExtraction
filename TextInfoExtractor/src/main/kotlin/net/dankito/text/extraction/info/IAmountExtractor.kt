package net.dankito.text.extraction.info

import net.dankito.text.extraction.info.model.AmountOfMoney


interface IAmountExtractor {

    open fun extractAmountsOfMoney(lines: List<String>): List<AmountOfMoney>

    open fun extractPercentages(lines: List<String>): List<AmountOfMoney>

}