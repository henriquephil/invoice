package dev.hphil.invoice.account.database

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class Banking(id: EntityID<UUID>) : UUIDEntity(id) {
    var accountId by BankingTable.accountId
    var beneficiaryName by BankingTable.beneficiaryName
    var beneficiaryAccountId by BankingTable.beneficiaryAccountId
    var intermediaryAccountId by BankingTable.intermediaryAccountId

    companion object : UUIDEntityClass<Banking>(BankingTable) {
        fun new(account: Account, beneficiaryAccount: BankAccount) = new {
            this.accountId = account.id
            this.beneficiaryName = ""
            this.beneficiaryAccountId = beneficiaryAccount.id
        }

        fun findByAccount(account: Account) = find {
            BankingTable.accountId eq account.id
        }.first()
    }
}

