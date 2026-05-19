package dev.hphil.invoice.commons.dtos.catalog

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class AddressDTO(
    val street: String,
    val number: String,
    val complement: String,
    val neighborhood: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String
)

@Serializable
data class UpdateAddressDTO(
    val street: String? = null,
    val number: String? = null,
    val complement: String? = null,
    val neighborhood: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val country: String? = null
)

@Serializable
data class CreateCustomerRequest(
    val name: String,
    val document: String,
    val email: String,
    val phone: String,
    val address: AddressDTO
)

@Serializable
data class UpdateCustomerRequest(
    val name: String? = null,
    val document: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: UpdateAddressDTO? = null
)

@Serializable
data class CreateItemRequest(
    val name: String,
    val type: String,
    val measureUnit: String,
    @Contextual
    val unitPrice: BigDecimal,
    val currency: String
)

@Serializable
class UpdateItemRequest(
    val name: String?,
    val type: String?,
    val measureUnit: String?,
    @Contextual
    val unitPrice: BigDecimal?,
    val currency: String?
)
