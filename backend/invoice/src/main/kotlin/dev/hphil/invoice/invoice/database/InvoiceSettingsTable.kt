package dev.hphil.invoice.invoice.database

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.core.java.javaUUID

object InvoiceSettingsTable : UUIDTable("invoice_settings") {
    val accountId = javaUUID("account_id").uniqueIndex("unique_invoice_settings_account_id")
    val currentInvoiceNumber = integer("current_invoice_number")
}