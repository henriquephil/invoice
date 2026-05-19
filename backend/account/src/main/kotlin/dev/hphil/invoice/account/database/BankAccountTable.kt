package dev.hphil.invoice.account.database

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable

object BankAccountTable : UUIDTable("bank_accounts") {
    val accountNumber = text("account_number")
    val swiftCode = text("swift_code")
    val bankName = text("bank_name")
    val bankAddress = text("bank_address")
}
