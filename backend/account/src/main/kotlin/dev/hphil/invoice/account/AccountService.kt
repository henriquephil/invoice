package dev.hphil.invoice.account

import dev.hphil.invoice.account.database.Account
import dev.hphil.invoice.account.database.Address
import dev.hphil.invoice.account.database.BankAccount
import dev.hphil.invoice.account.database.Banking
import dev.hphil.invoice.commons.dtos.account.CreateAccountRequest
import dev.hphil.invoice.commons.util.txWrite
import java.util.UUID

class AccountService {
    fun findById(accountId: UUID, userId: UUID): Account {
        return Account.findByIdAndOwnerUserId(accountId, userId)
            ?: throw NoSuchElementException("Account not found")
    }

    suspend fun initAccount(request: CreateAccountRequest, userId: UUID): Account {
        return txWrite {
            Account.new(userId, request.name, request.document, request.email, request.phone)
                .also {
                    Address.new(it)
                    Banking.new(it, BankAccount.new())
                }
        }
    }
}