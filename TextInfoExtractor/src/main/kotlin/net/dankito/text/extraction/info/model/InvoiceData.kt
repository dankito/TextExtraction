package net.dankito.text.extraction.info.model


// TODO: may move to package invoice
open class InvoiceData(
    val totalAmount: AmountOfMoney,
    val netAmount: AmountOfMoney? = null,
    val valueAddedTax: AmountOfMoney? = null,
    val valueAddedTaxRate: AmountOfMoney? = null
) {

    override fun toString(): String {
        return "" + totalAmount
    }

}