package dev.hphil.invoice.invoice.service

import dev.hphil.invoice.commons.dtos.account.AccountAddressResponse
import dev.hphil.invoice.commons.dtos.account.AccountBankingResponse
import dev.hphil.invoice.commons.dtos.account.AccountResponse
import dev.hphil.invoice.commons.dtos.catalog.CustomerResponse
import dev.hphil.invoice.commons.dtos.catalog.ItemResponse
import dev.hphil.invoice.commons.http.client.AccountHttpClient
import dev.hphil.invoice.commons.http.client.CatalogHttpClient
import dev.hphil.invoice.invoice.database.*
import dev.hphil.invoice.invoice.handler.BadInvoiceException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.toKotlinLocalDate
import java.util.*

class SnapshotService(
    private val accountHttpClient: AccountHttpClient,
    private val catalogHttpClient: CatalogHttpClient
) {

    fun build(invoice: Invoice, items: List<InvoiceItem>, externalSnapshots: ExternalSnapshots): InvoiceSnapshot {
        return InvoiceSnapshot(
            invoice.id.value,
            externalSnapshots.account,
            externalSnapshots.address,
            externalSnapshots.billing,
            invoice.createdAt,
            invoice.status,
            externalSnapshots.customer,
            invoice.number ?: throw BadInvoiceException("Draft invoice missing number [invoiceId: ${invoice.id.value}]"),
            invoice.issuedAt ?: throw BadInvoiceException("Draft invoice missing issued date [invoiceId: ${invoice.id.value}]"),
            (invoice.dueDate ?: throw BadInvoiceException("Draft invoice missing due date [invoiceId: ${invoice.id.value}]")).toKotlinLocalDate(),
            invoice.currency ?: throw BadInvoiceException("Draft invoice missing currency [invoiceId: ${invoice.id.value}]"),
            invoice.totalPrice,
            items.map { item ->
                val itemSnapshot = externalSnapshots.items[item.itemId] ?: throw BadInvoiceException("Item not found for invoice item [invoiceId: ${invoice.id.value}, invoiceItemId: ${item.id.value}, itemId: ${item.itemId}]")
                InvoiceItemSnapshot(
                    item.id.value,
                    itemSnapshot,
                    item.unitPrice,
                    item.quantity,
                    item.totalPrice,
                    item.additionalInfo
                )
            }
        )
    }

    suspend fun fetchExternalData(invoice: Invoice, items: List<InvoiceItem>, account: AccountResponse): ExternalSnapshots = coroutineScope {
        val addressDeferred = async { accountHttpClient.getAccountAddress(account.id) }
        val billingDeferred = async { accountHttpClient.getAccountBanking(account.id) }
        val customerDeferred = async { catalogHttpClient.getCustomer(invoice.customerId!!) }
        val itemsDeferred = items.map { async { catalogHttpClient.getItem(it.itemId) } } // TODO get by list of ids
        ExternalSnapshots(
            account,
            addressDeferred.await(),
            billingDeferred.await(),
            customerDeferred.await(),
            itemsDeferred.awaitAll().map { it }.associateBy { it.id }
        )
    }
}

data class ExternalSnapshots(
    val account: AccountResponse,
    val address: AccountAddressResponse,
    val billing: AccountBankingResponse,
    val customer: CustomerResponse,
    val items: Map<UUID, ItemResponse>
)
