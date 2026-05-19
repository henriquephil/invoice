package dev.hphil.invoice.commons.dtos.account

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountRequest(
    val name: String,
    val document: String,
    val email: String,
    val phone: String
)

@Serializable
data class UpdateAccountRequest(
    val name: String?,
    val document: String?,
    val email: String?,
    val phone: String?
)

@Serializable
data class UpdateAddressRequest(
    val street: String?,
    val number: String?,
    val complement: String?,
    val neighborhood: String?,
    val city: String?,
    val state: String?,
    val zipCode: String?,
    val country: String?
)

@Serializable
data class UpdateBankingRequest(
    val beneficiaryName: String?,
    val accountNumber: String?,
    val swiftCode: String?,
    val bankName: String?,
    val bankAddress: String?
)

@Serializable
data class UpdateIntermediaryRequest(
    val accountNumber: String?,
    val swiftCode: String?,
    val bankName: String?,
    val bankAddress: String?
)
