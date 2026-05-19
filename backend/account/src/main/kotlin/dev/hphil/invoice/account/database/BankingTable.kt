package dev.hphil.invoice.account.database

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable

object BankingTable : UUIDTable("banking") {
    val accountId = reference("account_id", AccountTable.id)
    val beneficiaryName = text("beneficiary_name")
    val beneficiaryAccountId = reference("beneficiary_account_id", BankAccountTable.id)
    val intermediaryAccountId = optReference("intermediary_account_id", BankAccountTable.id, onDelete = ReferenceOption.SET_NULL)
}
