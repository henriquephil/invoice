package dev.hphil.invoice.invoice.database

import dev.hphil.invoice.commons.util.CustomJson
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.javatime.timestampWithTimeZone
import org.jetbrains.exposed.v1.json.jsonb

object SnapshotTable : UUIDTable("snapshots") {
    val invoiceId = reference("invoice_id", InvoiceTable).index("index_snapshots_invoice_id")
    val capturedAt = timestampWithTimeZone("captured_at")
    val snapshot = jsonb<InvoiceSnapshot>("snapshot", CustomJson)
    val version = integer("version")
}
