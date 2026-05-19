package dev.hphil.invoice.invoice.service

import dev.hphil.invoice.commons.dtos.account.AccountResponse
import dev.hphil.invoice.commons.dtos.invoice.InvoiceStatus
import dev.hphil.invoice.invoice.database.Invoice
import dev.hphil.invoice.invoice.database.InvoiceItem
import java.util.*

class InvoiceService {
    fun getInvoiceForUpdate(invoiceId: UUID, account: AccountResponse): Invoice {
        val invoice = Invoice.findByIdAndAccountId(invoiceId, account.id) ?: throw NoSuchElementException("Invoice not found")
        return if (invoice.status == InvoiceStatus.DRAFT) invoice else throw IllegalStateException("Can only update a draft invoice")
    }

    fun recalculateTotalPrice(invoice: Invoice) {
        invoice.totalPrice = InvoiceItem.findByInvoice(invoice).sumOf { it.totalPrice }
    }
}
