package dev.hphil.invoice.invoice.database

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.time.OffsetDateTime
import java.util.*

class Snapshot(id: EntityID<UUID>) : UUIDEntity(id) {
    var invoiceId by SnapshotTable.invoiceId
    var capturedAt by SnapshotTable.capturedAt
    var snapshot by SnapshotTable.snapshot
    var version by SnapshotTable.version

    companion object : UUIDEntityClass<Snapshot>(SnapshotTable) {
        fun new(invoice: Invoice, snapshot: InvoiceSnapshot, version: Int) = new {
            this.invoiceId = invoice.id
            this.capturedAt = OffsetDateTime.now()
            this.snapshot = snapshot
            this.version = version
        }

        fun findByInvoice(invoice: Invoice) = find {
            SnapshotTable.invoiceId eq invoice.id
        }.firstOrNull()
    }
}
