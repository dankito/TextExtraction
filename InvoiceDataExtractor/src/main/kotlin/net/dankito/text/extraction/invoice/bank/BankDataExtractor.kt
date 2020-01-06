package net.dankito.text.extraction.invoice.bank

import net.dankito.text.extraction.invoice.model.BankData


open class BankDataExtractor @JvmOverloads constructor(
    protected val ibanExtractor: IIbanExtractor = IbanExtractor(),
    protected val bicExtractor: IBicExtractor = BicExtractor()
) : IBankDataExtractor {


    override fun extractBankData(text: String): BankData {
        return extractBankData(text.split("\n"))
    }

    override fun extractBankData(lines: List<String>): BankData {
        return BankData(ibanExtractor.extractIbans(lines), bicExtractor.extractBics(lines))
    }

}