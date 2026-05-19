package dev.hphil.invoice.commons.dtos.invoice

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.*

@Serializable
data class UpdateInvoiceRequest(
    @Contextual
    val customerId: UUID? = null,
    @Contextual
    val dueDate: LocalDate? = null,
    val currency: String? = null
)

@Serializable
data class CreateInvoiceItemRequest(
    @Contextual
    val itemId: UUID,
    @Contextual
    val unitPrice: BigDecimal,
    @Contextual
    val quantity: BigDecimal,
    val additionalInfo: String
)

@Serializable
data class UpdateInvoiceItemRequest(
    @Contextual
    val itemId: UUID? = null,
    @Contextual
    val unitPrice: BigDecimal?,
    @Contextual
    val quantity: BigDecimal? = null,
    val additionalInfo: String? = null
)

@Serializable
data class InvoiceSettingsRequest(
    val currentInvoiceNumber: Int? = null
)
