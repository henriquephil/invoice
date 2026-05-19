package dev.hphil.invoice.invoice.database

import dev.hphil.invoice.commons.dtos.account.AccountAddressResponse
import dev.hphil.invoice.commons.dtos.account.AccountBankingResponse
import dev.hphil.invoice.commons.dtos.account.AccountResponse
import dev.hphil.invoice.commons.dtos.catalog.CustomerResponse
import dev.hphil.invoice.commons.dtos.catalog.ItemResponse
import dev.hphil.invoice.commons.dtos.invoice.InvoiceStatus
import kotlinx.datetime.LocalDate
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID


data class InvoiceSnapshot(
    val id: UUID,
    val account: AccountResponse,
    val address: AccountAddressResponse,
    val billing: AccountBankingResponse,
    val createdAt: OffsetDateTime,
    val status: InvoiceStatus,
    val customer: CustomerResponse,
    val number: Int,
    val issuedAt: OffsetDateTime,
    val dueDate: LocalDate,
    val currency: String,
    val totalPrice: BigDecimal,
    val items: List<InvoiceItemSnapshot>
)

data class InvoiceItemSnapshot(
    val id: UUID,
    val item: ItemResponse,
    val unitPrice: BigDecimal,
    val quantity: BigDecimal,
    val totalPrice: BigDecimal,
    val additionalInfo: String
)
