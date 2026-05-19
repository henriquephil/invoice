package dev.hphil.invoice.invoice.database

import dev.hphil.invoice.commons.dtos.invoice.InvoiceStatus
import dev.hphil.invoice.commons.util.BigDecimalSerializer
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

class Invoice(id: EntityID<UUID>) : UUIDEntity(id) {
    var accountId by InvoiceTable.accountId
    var createdAt by InvoiceTable.createdAt
    var status by InvoiceTable.status
    var customerId by InvoiceTable.customerId
    var dueDate by InvoiceTable.dueDate
    var number by InvoiceTable.number
    var issuedAt by InvoiceTable.issuedAt
    var currency by InvoiceTable.currency
    var totalPrice by InvoiceTable.totalPrice

    companion object : UUIDEntityClass<Invoice>(InvoiceTable) {
        fun new(accountId: UUID) = new {
            this.accountId = accountId
            this.status = InvoiceStatus.DRAFT
            this.createdAt = OffsetDateTime.now()
            this.totalPrice = BigDecimal.ZERO
        }

        fun findByAccountId(accountId: UUID) = find {
            InvoiceTable.accountId eq accountId
        }.sortedByDescending {
            InvoiceTable.createdAt
        }.toList()

        fun findByIdAndAccountId(id: UUID, accountId: UUID) = find {
            (InvoiceTable.id eq id) and (InvoiceTable.accountId eq accountId)
        }.firstOrNull()
    }
}
