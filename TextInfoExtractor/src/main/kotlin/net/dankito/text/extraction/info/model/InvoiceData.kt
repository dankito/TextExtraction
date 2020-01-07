package net.dankito.text.extraction.info.model


// TODO: may move to package invoice
open class InvoiceData(
    val allAmounts: List<AmountOfMoney>,
    val dates: List<DateData>,
    val ibans: List<StringSearchResult>,
    val bics: List<StringSearchResult>,
    val potentialTotalAmount: AmountOfMoney? = null,
    val potentialNetAmount: AmountOfMoney? = null,
    val potentialValueAddedTax: AmountOfMoney? = null,
    val potentialValueAddedTaxRate: AmountOfMoney? = null,
    val error: Exception? = null
) {

    companion object {
        fun couldNotExtractInvoiceData(error: Exception?): InvoiceData {
            return InvoiceData(listOf(), listOf(), listOf(), listOf(), null, null, null, null, error)
        }
    }


    val couldExtractInvoiceData: Boolean
        get() = allAmounts.isNotEmpty() && error == null


    override fun toString(): String {
        if (couldExtractInvoiceData == false) {
            return "Could not extract invoice data, error = $error"
        }

        return "Could extract invoice data, totalAmount = $potentialTotalAmount, allAmounts = $allAmounts"
    }

}