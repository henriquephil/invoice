package dev.hphil.invoice.catalog

import dev.hphil.invoice.catalog.database.Customer
import dev.hphil.invoice.commons.dtos.account.AccountResponse
import dev.hphil.invoice.commons.dtos.catalog.AddressDTO
import dev.hphil.invoice.commons.dtos.catalog.CreateCustomerRequest
import dev.hphil.invoice.commons.dtos.catalog.CustomerResponse
import dev.hphil.invoice.commons.dtos.catalog.UpdateCustomerRequest
import dev.hphil.invoice.commons.util.txRead
import dev.hphil.invoice.commons.util.txWrite
import java.util.UUID

class CustomerService {
    suspend fun create(request: CreateCustomerRequest, account: AccountResponse): UUID {
        // todo validate
        return txWrite {
            Customer.new(
                account.id,
                request.name,
                request.document,
                request.email,
                request.phone,
                request.address
            )
        }.id.value
    }

    suspend fun getAll(account: AccountResponse): List<CustomerResponse> {
        return txRead { Customer.findByAccountId(account.id) }
            .map { it.toCustomerResponse() }
    }

    suspend fun get(id: UUID, account: AccountResponse): CustomerResponse {
        val customer = txRead { Customer.findByIdAndAccountId(id, account.id) }
            ?: throw NoSuchElementException("Customer not found")
        return customer.toCustomerResponse()
    }

    suspend fun update(id: UUID, request: UpdateCustomerRequest, account: AccountResponse) {
        txWrite {
            Customer.findByIdAndAccountId(id, account.id)?.apply {
                request.name?.let { name = it }
                request.document?.let { document = it }
                request.email?.let { email = it }
                request.phone?.let { phone = it }
                request.address?.let {
                    it.street?.let { street = it }
                    it.number?.let { number = it }
                    it.complement?.let { complement = it }
                    it.neighborhood?.let { neighborhood = it }
                    it.city?.let { city = it }
                    it.state?.let { state = it }
                    it.zipCode?.let { zipCode = it }
                    it.country?.let { country = it }
                }
            }
        }
    }
}

private fun Customer.toCustomerResponse(): CustomerResponse {
    return CustomerResponse(
        id.value,
        name,
        document,
        email,
        phone,
        AddressDTO(
            street,
            number,
            complement ?: "",
            neighborhood,
            city,
            state,
            zipCode,
            country
        )
    )
}
