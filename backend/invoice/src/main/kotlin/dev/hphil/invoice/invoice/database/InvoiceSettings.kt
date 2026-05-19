package dev.hphil.invoice.invoice.database

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class InvoiceSettings(id: EntityID<UUID>) : UUIDEntity(id) {
    var accountId by InvoiceSettingsTable.accountId
    var currentInvoiceNumber by InvoiceSettingsTable.currentInvoiceNumber

    companion object : UUIDEntityClass<InvoiceSettings>(InvoiceSettingsTable) {
        fun new(accountId: UUID) = new {
            this.accountId = accountId
            this.currentInvoiceNumber = 0
        }

        fun findByAccountId(accountId: UUID) = find { InvoiceSettingsTable.accountId eq accountId }
            .forUpdate()
            .firstOrNull()
    }
}
