package dev.hphil.invoice.invoice.handler

import dev.hphil.invoice.commons.dtos.account.AccountResponse
import dev.hphil.invoice.commons.dtos.invoice.InvoiceStatus
import dev.hphil.invoice.commons.util.txRead
import dev.hphil.invoice.commons.util.txWrite
import dev.hphil.invoice.invoice.database.Invoice
import dev.hphil.invoice.invoice.database.InvoiceItem
import dev.hphil.invoice.invoice.database.Snapshot
import dev.hphil.invoice.invoice.service.InvoiceService
import dev.hphil.invoice.invoice.service.SettingsService
import dev.hphil.invoice.invoice.service.SnapshotService
import java.time.OffsetDateTime
import java.util.*

class IssueHandler(
    private val invoiceService: InvoiceService,
    private val settingsService: SettingsService,
    private val snapshotService: SnapshotService
) {

    suspend fun issue(invoiceId: UUID, account: AccountResponse): Snapshot {
        val (invoice, items) = txRead {
            val invoice = invoiceService.getInvoiceForUpdate(invoiceId, account)
            invoice to InvoiceItem.findByInvoice(invoice)
        }
        validate(invoice, items)
        val snapshotData = snapshotService.fetchExternalData(invoice, items, account)
        return txWrite {
            val issued = invoiceService.getInvoiceForUpdate(invoiceId, account).apply {
                status = InvoiceStatus.ISSUED
                number = settingsService.numberIncrementAndGet(account)
                issuedAt = OffsetDateTime.now()
            }
            val snapshot = snapshotService.build(issued, items, snapshotData)
            Snapshot.new(issued, snapshot, CURRENT_SNAPSHOT_VERSION)
        }
    }

    private fun validate(invoice: Invoice, items: List<InvoiceItem>) {
        if (invoice.customerId == null) throw IllegalStateException("Cannot issue an invoice without a customer")
        if (invoice.dueDate == null) throw IllegalStateException("Cannot issue an invoice with no due date")
        if (items.isEmpty()) throw IllegalStateException("Cannot issue an empty invoice")
        items.firstOrNull { it.totalPrice != (it.unitPrice * it.quantity) }?.also {
            throw IllegalStateException("Invoice item ${it.id.value} has invalid total price")
        }
        if (items.sumOf { it.totalPrice } != invoice.totalPrice) throw IllegalStateException("Invoice has invalid total price")
    }

    companion object {
        private const val CURRENT_SNAPSHOT_VERSION = 1
    }
}