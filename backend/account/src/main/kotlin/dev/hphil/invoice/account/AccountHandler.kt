package dev.hphil.invoice.account

import dev.hphil.invoice.account.database.Account
import dev.hphil.invoice.commons.dtos.account.AccountResponse
import dev.hphil.invoice.commons.dtos.account.CreateAccountRequest
import dev.hphil.invoice.commons.dtos.account.UpdateAccountRequest
import dev.hphil.invoice.commons.util.txRead
import dev.hphil.invoice.commons.util.txWrite
import java.util.UUID

class AccountHandler(
    private val accountService: AccountService
) {
    suspend fun create(request: CreateAccountRequest, userId: UUID): UUID {
        // validate request
        return accountService.initAccount(request, userId).id.value
    }

    suspend fun getAll(userId: UUID): List<AccountResponse> {
        return txRead { Account.findByOwnerUserId(userId) }
            .map { it.toAccountResponse() }
    }

    suspend fun get(id: UUID, userId: UUID): AccountResponse {
        val account = txRead { accountService.findById(id, userId) }
        return account.toAccountResponse()
    }

    suspend fun update(id: UUID, request: UpdateAccountRequest, userId: UUID) {
        // validate request
        txWrite {
            val account = accountService.findById(id, userId)
            request.name?.let { account.name = it }
            request.document?.let { account.document = it }
            request.email?.let { account.email = it }
            request.phone?.let { account.phone = it }
        }
    }
}

private fun Account.toAccountResponse() = AccountResponse(id.value, name, document, email, phone)
