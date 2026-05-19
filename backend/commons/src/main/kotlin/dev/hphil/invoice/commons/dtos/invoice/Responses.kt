package dev.hphil.invoice.commons.dtos.invoice

import dev.hphil.invoice.commons.dtos.account.AccountAddressResponse
import dev.hphil.invoice.commons.dtos.account.AccountBankingResponse
import dev.hphil.invoice.commons.dtos.account.AccountResponse
import dev.hphil.invoice.commons.dtos.catalog.CustomerResponse
import dev.hphil.invoice.commons.dtos.catalog.ItemResponse
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Serializable
data class InvoiceSettingsResponse(
    val currentInvoiceNumber: Int
)

@Serializable
data class InvoiceHeadResponse(
    @Contextual
    val id: UUID,
    val status: InvoiceStatus,
    val number: Int?,
    val customerName: String?,
    @Contextual
    val createdAt: OffsetDateTime, // todo test with kotlin.time.Instant
    @Contextual
    val issuedAt: OffsetDateTime?,
    @Contextual
    val dueDate: LocalDate?,
    @Contextual
    val totalPrice: BigDecimal
)


@Serializable
@JsonClassDiscriminator("status")
sealed class InvoiceResponse {
    @Contextual
    abstract val id: UUID
    abstract val status: InvoiceStatus
    @Contextual
    abstract val createdAt: OffsetDateTime
}


@Serializable
@SerialName("DRAFT")
data class DraftInvoiceResponse(
    @Contextual
    override val id: UUID,
    override val status: InvoiceStatus,
    @Contextual
    override val createdAt: OffsetDateTime,
    @Contextual
    val customerId: UUID?,
    val dueDate: LocalDate?,
    val currency: String?,
    @Contextual
    val totalPrice: BigDecimal,
    val items: List<DraftInvoiceResponseItem>
) : InvoiceResponse()

@Serializable
data class DraftInvoiceResponseItem(
    @Contextual
    val id: UUID,
    @Contextual
    val itemId: UUID,
    @Contextual
    val unitPrice: BigDecimal,
    @Contextual
    val quantity: BigDecimal,
    @Contextual
    val totalPrice: BigDecimal,
    val additionalInfo: String
)

@Serializable
@SerialName("ISSUED")
data class IssuedInvoiceResponse(
    @Contextual
    override val id: UUID,
    override val status: InvoiceStatus,
    @Contextual
    override val createdAt: OffsetDateTime,
    val account: AccountResponse,
    val address: AccountAddressResponse,
    val billing: AccountBankingResponse,
    val customer: CustomerResponse,
    val number: Int,
    @Contextual
    val issuedAt: OffsetDateTime,
    val dueDate: LocalDate,
    val currency: String,
    @Contextual
    val totalPrice: BigDecimal,
    val items: List<IssuedInvoiceResponseItem>
) : InvoiceResponse()

@Serializable
data class IssuedInvoiceResponseItem(
    @Contextual
    val id: UUID,
    val item: ItemResponse,
    @Contextual
    val unitPrice: BigDecimal,
    @Contextual
    val quantity: BigDecimal,
    @Contextual
    val totalPrice: BigDecimal,
    val additionalInfo: String
)
