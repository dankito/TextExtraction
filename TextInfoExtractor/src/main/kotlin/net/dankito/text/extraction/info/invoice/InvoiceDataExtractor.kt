package net.dankito.text.extraction.info.invoice

import net.dankito.text.extraction.info.*
import net.dankito.text.extraction.info.bank.BicExtractor
import net.dankito.text.extraction.info.bank.IBicExtractor
import net.dankito.text.extraction.info.bank.IIbanExtractor
import net.dankito.text.extraction.info.bank.IbanExtractor
import net.dankito.text.extraction.info.model.InvoiceData
import org.slf4j.LoggerFactory


open class InvoiceDataExtractor @JvmOverloads constructor(
    protected val amountExtractor: IAmountExtractor = AmountExtractor(),
    protected val amountCategorizer: IAmountCategorizer = AmountCategorizer(),
    protected val dateExtractor: DateExtractor = DateExtractor(),
    protected val ibanExtractor: IIbanExtractor = IbanExtractor(),
    protected val bicExtractor: IBicExtractor = BicExtractor()
) : ExtractorBase(), IInvoiceDataExtractor {

    companion object {
        private val log = LoggerFactory.getLogger(InvoiceDataExtractor::class.java)
    }


    override fun extractInvoiceData(text: String): InvoiceData {
        return extractInvoiceData(getLines(text))
    }

    override fun extractInvoiceData(lines: List<String>): InvoiceData {
        try {
            val percentages = amountExtractor.extractPercentages(lines)

            val potentialVatRate = amountCategorizer.findValueAddedTaxRate(percentages)

            val amounts = amountExtractor.extractAmountsOfMoney(lines)

            val dates = dateExtractor.extractDates(lines)

            val ibans = ibanExtractor.extractIbans(lines)

            val bics = bicExtractor.extractBics(lines)

            amountCategorizer.findTotalNetAndVatAmount(amounts)?.let { potentialAmounts ->
                return InvoiceData(amounts, dates, ibans, bics, potentialAmounts.totalAmount, potentialAmounts.netAmount, potentialAmounts.valueAddedTax, potentialVatRate)
            }

            return InvoiceData(amounts, dates, ibans, bics)
        } catch (e: Exception) {
            log.error("Could not extract invoice data from:${lines.map { System.lineSeparator() + it }}", e)

            return InvoiceData.couldNotExtractInvoiceData(e)
        }
    }

}