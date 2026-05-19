package dev.hphil.invoice.commons.dtos.catalog

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.UUID

@Serializable
data class CustomerResponse(
    @Contextual
    val id: UUID,
    val name: String,
    val document: String,
    val email: String,
    val phone: String,
    val address: AddressDTO
)

@Serializable
class ItemResponse(
    @Contextual
    val id: UUID,
    val type: String,
    val name: String,
    val measureUnit: String,
    @Contextual
    val unitPrice: BigDecimal,
    val currency: String
)
