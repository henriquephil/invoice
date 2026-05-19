package dev.hphil.invoice.invoice.database

import dev.hphil.invoice.commons.dtos.invoice.InvoiceStatus
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.date
import org.jetbrains.exposed.v1.javatime.timestampWithTimeZone

object InvoiceTable : UUIDTable("invoices") {
    val accountId = javaUUID("account_id").index("index_invoices_account_id")
    val createdAt = timestampWithTimeZone("created_at")
    val status = enumerationByName<InvoiceStatus>("status", 20)
    val customerId = javaUUID("customer_id").index("index_invoices_customer_id").nullable()
    val dueDate = date("due_date").nullable()
    val number = integer("number").nullable()
    val issuedAt = timestampWithTimeZone("issued_at").nullable()
    val currency = text("currency").nullable()
    val totalPrice = decimal("total_price", 10, 2)
}
