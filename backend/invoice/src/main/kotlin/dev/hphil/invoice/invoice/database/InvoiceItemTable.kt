package dev.hphil.invoice.invoice.database

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.core.java.javaUUID

object InvoiceItemTable : UUIDTable("invoice_items") {
    val invoiceId = reference("invoice_id", InvoiceTable, onDelete = ReferenceOption.CASCADE)
    val itemId = javaUUID("item_id")
    val unitPrice = decimal("unit_price", 10, 2)
    val quantity = decimal("quantity", 10, 2)
    val totalPrice = decimal("total_price", 10, 2)
    val additionalInfo = text("additional_info")
}