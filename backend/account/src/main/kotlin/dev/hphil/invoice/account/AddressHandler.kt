package dev.hphil.invoice.account

import dev.hphil.invoice.account.database.Address
import dev.hphil.invoice.commons.dtos.account.AccountAddressResponse
import dev.hphil.invoice.commons.dtos.account.UpdateAddressRequest
import dev.hphil.invoice.commons.util.txRead
import dev.hphil.invoice.commons.util.txWrite
import java.util.*

class AddressHandler(
    private val accountService: AccountService
) {
    suspend fun get(accountId: UUID, userId: UUID): AccountAddressResponse {
        val address = txRead {
            val account = accountService.findById(accountId, userId)
            Address.findByAccount(account)
        }
        return AccountAddressResponse(
            address.street,
            address.number,
            address.complement,
            address.neighborhood,
            address.city,
            address.state,
            address.zipCode,
            address.country
        )
    }

    suspend fun update(accountId: UUID, request: UpdateAddressRequest, userId: UUID) {
        txWrite {
            val account = accountService.findById(accountId, userId)
            val address = Address.findByAccount(account)
            request.street?.let { address.street = it }
            request.number?.let { address.number = it }
            request.complement?.let { address.complement = it }
            request.neighborhood?.let { address.neighborhood = it }
            request.city?.let { address.city = it }
            request.state?.let { address.state = it }
            request.zipCode?.let { address.zipCode = it }
            request.country?.let { address.country = it }
        }
    }
}
