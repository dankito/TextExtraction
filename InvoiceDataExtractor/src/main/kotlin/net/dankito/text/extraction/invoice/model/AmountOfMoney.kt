package net.dankito.text.extraction.invoice.model


class AmountOfMoney(val amount: Double, val currency: String, val amountWithCurrency: String, val foundInLine: String) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AmountOfMoney

        if (amount != other.amount) return false
        if (currency != other.currency) return false
        if (amountWithCurrency != other.amountWithCurrency) return false

        return true
    }

    override fun hashCode(): Int {
        var result = amount.hashCode()
        result = 31 * result + currency.hashCode()
        result = 31 * result + amountWithCurrency.hashCode()
        return result
    }



    override fun toString(): String {
        return amountWithCurrency
    }

}