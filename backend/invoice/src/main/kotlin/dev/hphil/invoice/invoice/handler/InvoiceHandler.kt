package dev.hphil.invoice.invoice.handler

import dev.hphil.invoice.commons.dtos.account.AccountResponse
import dev.hphil.invoice.commons.dtos.invoice.*
import dev.hphil.invoice.commons.http.client.CatalogHttpClient
import dev.hphil.invoice.commons.util.txRead
import dev.hphil.invoice.commons.util.txWrite
import dev.hphil.invoice.invoice.database.Invoice
import dev.hphil.invoice.invoice.database.InvoiceItem
import dev.hphil.invoice.invoice.database.Snapshot
import dev.hphil.invoice.invoice.service.InvoiceService
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.util.*

class InvoiceHandler(
    private val catalogHttpClient: CatalogHttpClient,
    private val invoiceService: InvoiceService
) {
    suspend fun create(account: AccountResponse): UUID {
        return txWrite {
            Invoice.new(account.id)
        }.id.value
    }

    suspend fun update(invoiceId: UUID, request: UpdateInvoiceRequest, account: AccountResponse) {
        txWrite {
            val invoice = invoiceService.getInvoiceForUpdate(invoiceId, account)
            request.customerId?.let { invoice.customerId = it }
            request.dueDate?.let { invoice.dueDate = it.toJavaLocalDate() }
            request.currency?.let { invoice.currency = it }
        }
    }

    suspend fun delete(invoiceId: UUID, account: AccountResponse) {
        txWrite {
            val invoice =
                Invoice.findByIdAndAccountId(invoiceId, account.id) ?: throw NoSuchElementException("Invoice not found")
            if (invoice.status == InvoiceStatus.DRAFT) {
                invoice.delete()
            } else {
                invoice.status = InvoiceStatus.DELETED
            }
        }
    }

    suspend fun list(account: AccountResponse): List<InvoiceHeadResponse> {
        val invoices = txRead { Invoice.findByAccountId(account.id) }
        return invoices.map { invoice ->
            if (invoice.status == InvoiceStatus.DRAFT) {
                // todo do not http inside loop + get by id set
                val customerName = invoice.customerId?.let { catalogHttpClient.getCustomer(it).name }
                InvoiceHeadResponse(
                    invoice.id.value,
                    invoice.status,
                    invoice.number,
                    customerName,
                    invoice.createdAt,
                    invoice.issuedAt,
                    invoice.dueDate?.toKotlinLocalDate(),
                    invoice.totalPrice
                )
            } else {
                val snapshot = txRead {
                    Snapshot.findByInvoice(invoice)?.snapshot
                } ?: throw NoSuchElementException("Snapshot not found [invoiceId: ${invoice.id.value}]")
                InvoiceHeadResponse(
                    snapshot.id,
                    snapshot.status,
                    snapshot.number,
                    snapshot.customer.name,
                    snapshot.createdAt,
                    snapshot.issuedAt,
                    snapshot.dueDate,
                    snapshot.totalPrice
                )
            }
        }
    }

    suspend fun find(invoiceId: UUID, account: AccountResponse): InvoiceResponse {
        val invoice = txRead {
            Invoice.findByIdAndAccountId(invoiceId, account.id)
        } ?: throw NoSuchElementException("Invoice not found [invoiceId: ${invoiceId}]")
        return if (invoice.status == InvoiceStatus.DRAFT) {
            val items = txRead { InvoiceItem.findByInvoice(invoice) }
            DraftInvoiceResponse(
                invoice.id.value,
                invoice.status,
                invoice.createdAt,
                invoice.customerId,
                invoice.dueDate?.toKotlinLocalDate(),
                invoice.currency,
                invoice.totalPrice,
                items.map {
                    DraftInvoiceResponseItem(
                        it.id.value,
                        it.itemId,
                        it.unitPrice,
                        it.quantity,
                        it.totalPrice,
                        it.additionalInfo
                    )
                }
            )
        } else {
            val snapshot = txRead {
                Snapshot.findByInvoice(invoice)?.snapshot
            } ?: throw NoSuchElementException("Invoice snapshot not found [invoiceId: ${invoiceId}]")
            IssuedInvoiceResponse(
                snapshot.id,
                snapshot.status,
                snapshot.createdAt,
                snapshot.account,
                snapshot.address,
                snapshot.billing,
                snapshot.customer,
                snapshot.number,
                snapshot.issuedAt,
                snapshot.dueDate,
                snapshot.currency,
                snapshot.totalPrice,
                snapshot.items.map { item ->
                    IssuedInvoiceResponseItem(
                        item.id,
                        item.item,
                        item.unitPrice,
                        item.quantity,
                        item.totalPrice,
                        item.additionalInfo
                    )
                }
            )
        }
    }
}
