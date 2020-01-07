package net.dankito.text.extraction.info.model


// TODO: may move to package invoice
open class InvoiceData(
    val totalAmount: AmountOfMoney,
    val netAmount: AmountOfMoney? = null,
    val valueAddedTax: AmountOfMoney? = null,
    val valueAddedTaxRate: AmountOfMoney? = null,
    val error: Exception? = null
) {

    companion object {
        fun couldNotExtractInvoiceData(error: Exception?): InvoiceData {
            return InvoiceData(AmountOfMoney(0.0, "", "", ""), null, null, null, error)
        }
    }


    val couldExtractInvoiceData: Boolean
        get() = error == null


    override fun toString(): String {
        if (couldExtractInvoiceData == false) {
            return "Could not extract invoice data, error = $error"
        }

        return "Could extract invoice data, totalAmount = $totalAmount"
    }

}