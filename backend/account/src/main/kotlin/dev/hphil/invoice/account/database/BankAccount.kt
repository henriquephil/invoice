package dev.hphil.invoice.account.database

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class BankAccount(id: EntityID<UUID>) : UUIDEntity(id) {
    var accountNumber by BankAccountTable.accountNumber
    var swiftCode by BankAccountTable.swiftCode
    var bankName by BankAccountTable.bankName
    var bankAddress by BankAccountTable.bankAddress

    companion object : UUIDEntityClass<BankAccount>(BankAccountTable) {
        fun new(
            accountNumber: String = "",
            swiftCode: String = "",
            bankName: String = "",
            bankAddress: String = ""
        ) = new {
            this.accountNumber = accountNumber
            this.swiftCode = swiftCode
            this.bankName = bankName
            this.bankAddress = bankAddress
        }
    }
}

