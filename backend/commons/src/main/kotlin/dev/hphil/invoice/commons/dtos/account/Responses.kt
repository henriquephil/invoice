package dev.hphil.invoice.commons.dtos.account

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
data class AccountResponse(
    @Contextual
    val id: UUID,
    val name: String,
    val document: String,
    val email: String,
    val phone: String
)

@Serializable
data class AccountAddressResponse(
    val street: String = "",
    val number: String = "",
    val complement: String = "",
    val neighborhood: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = ""       // "BR" ou "US" (ISO 3166-1 alpha-2)
)

@Serializable
data class AccountBankingResponse(
    val beneficiaryName: String = "",
    val beneficiaryAccount: BankAccountDto = BankAccountDto(),
    val intermediaryAccount: BankAccountDto? = null
)

@Serializable
data class BankAccountDto(
    val accountNumber: String = "",
    val swiftCode: String = "",
    val bankName: String = "",
    val bankAddress: String = ""
)
