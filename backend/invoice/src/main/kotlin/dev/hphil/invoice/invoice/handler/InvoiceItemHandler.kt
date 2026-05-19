package dev.hphil.invoice.invoice.handler

import dev.hphil.invoice.commons.dtos.account.AccountResponse
import dev.hphil.invoice.commons.dtos.invoice.CreateInvoiceItemRequest
import dev.hphil.invoice.commons.dtos.invoice.UpdateInvoiceItemRequest
import dev.hphil.invoice.commons.util.txWrite
import dev.hphil.invoice.invoice.database.InvoiceItem
import dev.hphil.invoice.invoice.service.InvoiceService
import java.util.*

class InvoiceItemHandler(
    private val invoiceService: InvoiceService
) {
    suspend fun create(invoiceId: UUID, request: CreateInvoiceItemRequest, account: AccountResponse): UUID {
        return txWrite {
            val invoice = invoiceService.getInvoiceForUpdate(invoiceId, account)
            InvoiceItem.new(invoice, request.itemId, request.unitPrice, request.quantity, request.additionalInfo)
                .also { invoiceService.recalculateTotalPrice(invoice) }
        }.id.value
    }

    suspend fun update(invoiceId: UUID, itemId: UUID, request: UpdateInvoiceItemRequest, account: AccountResponse) {
        txWrite {
            val invoice = invoiceService.getInvoiceForUpdate(invoiceId, account)
            val invoiceItem = InvoiceItem.findByIdAndInvoice(itemId, invoice)
                ?: throw NoSuchElementException("Invoice item not found")
            request.quantity?.let { invoiceItem.quantity = it }
            request.unitPrice?.let { invoiceItem.unitPrice = it }
            request.additionalInfo?.let { invoiceItem.additionalInfo = it }
            invoiceItem.totalPrice = invoiceItem.unitPrice * invoiceItem.quantity
            invoiceService.recalculateTotalPrice(invoice)
        }
    }

    suspend fun delete(invoiceId: UUID, itemId: UUID, account: AccountResponse) {
        txWrite {
            val invoice = invoiceService.getInvoiceForUpdate(invoiceId, account)
            val item = InvoiceItem.findByIdAndInvoice(itemId, invoice)
                ?: throw NoSuchElementException("Invoice item not found")
            item.delete()
            invoiceService.recalculateTotalPrice(invoice)
        }
    }
}