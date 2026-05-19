package dev.hphil.invoice.account

import dev.hphil.invoice.account.database.BankAccount
import dev.hphil.invoice.account.database.Banking
import dev.hphil.invoice.commons.dtos.account.BankAccountDto
import dev.hphil.invoice.commons.dtos.account.AccountBankingResponse
import dev.hphil.invoice.commons.dtos.account.UpdateBankingRequest
import dev.hphil.invoice.commons.dtos.account.UpdateIntermediaryRequest
import dev.hphil.invoice.commons.util.txRead
import dev.hphil.invoice.commons.util.txWrite
import java.util.UUID

class BankingHandler(
    private val accountService: AccountService
) {
    suspend fun get(accountId: UUID, userId: UUID): AccountBankingResponse {
        return txRead {
            val banking = getBanking(accountId, userId)
            val beneficiary = BankAccount.findById(banking.beneficiaryAccountId)!!
            val intermediary = banking.intermediaryAccountId?.let { BankAccount.findById(it) }
            AccountBankingResponse(
                banking.beneficiaryName,
                BankAccountDto(
                    beneficiary.accountNumber,
                    beneficiary.swiftCode,
                    beneficiary.bankName,
                    beneficiary.bankAddress
                ),
                intermediary?.let {
                    BankAccountDto(
                        it.accountNumber,
                        it.swiftCode,
                        it.bankName,
                        it.bankAddress
                    )
                }
            )
        }
    }

    suspend fun update(accountId: UUID, request: UpdateBankingRequest, userId: UUID) {
        txWrite {
            val banking = getBanking(accountId, userId)
            val beneficiaryAccount = BankAccount.findById(banking.beneficiaryAccountId)!!
            request.beneficiaryName?.let { banking.beneficiaryName = it }
            request.accountNumber?.let { beneficiaryAccount.accountNumber = it }
            request.swiftCode?.let { beneficiaryAccount.swiftCode = it }
            request.bankName?.let { beneficiaryAccount.bankName = it }
            request.bankAddress?.let { beneficiaryAccount.bankAddress = it }
        }
    }

    suspend fun updateIntermediary(accountId: UUID, request: UpdateIntermediaryRequest, userId: UUID) {
        txWrite {
            val banking = getBanking(accountId, userId)
            val intermediary = banking.intermediaryAccountId?.let {
                BankAccount.findById(it)
            } ?: BankAccount.new().also { banking.intermediaryAccountId = it.id }

            request.accountNumber?.let { intermediary.accountNumber = it }
            request.swiftCode?.let { intermediary.swiftCode = it }
            request.bankName?.let { intermediary.bankName = it }
            request.bankAddress?.let { intermediary.bankAddress = it }
        }
    }

    suspend fun deleteIntermediary(accountId: UUID, userId: UUID) {
        txWrite {
            getBanking(accountId, userId).intermediaryAccountId?.let {
                BankAccount.findById(it)?.delete()
            }
        }
    }

    private fun getBanking(accountId: UUID, userId: UUID): Banking {
        val account = accountService.findById(accountId, userId)
        return Banking.findByAccount(account)
    }
}
