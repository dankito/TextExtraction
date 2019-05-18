package net.dankito.text.extraction.invoice.model


class Invoice(val totalAmount: AmountOfMoney,
              val netAmout: AmountOfMoney? = null,
              val valueAddedTax: AmountOfMoney? = null,
              val valueAddedTaxRate: AmountOfMoney? = null
) {

    override fun toString(): String {
        return "" + totalAmount
    }

}