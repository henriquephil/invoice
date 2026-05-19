package dev.hphil.invoice.invoice.database

import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.math.BigDecimal
import java.util.*

class InvoiceItem(id: EntityID<UUID>) : UUIDEntity(id) {
    var invoice by InvoiceItemTable.invoiceId
    var itemId by InvoiceItemTable.itemId
    var unitPrice by InvoiceItemTable.unitPrice
    var quantity by InvoiceItemTable.quantity
    var totalPrice by InvoiceItemTable.totalPrice
    var additionalInfo by InvoiceItemTable.additionalInfo

    companion object : UUIDEntityClass<InvoiceItem>(InvoiceItemTable) {
        fun new(invoice: Invoice, itemId: UUID, unitPrice: BigDecimal, quantity: BigDecimal, additionalInfo: String) = new {
            this.invoice = invoice.id
            this.itemId = itemId
            this.unitPrice = unitPrice
            this.quantity = quantity
            this.totalPrice = unitPrice * quantity
            this.additionalInfo = additionalInfo
        }

        fun findByInvoice(invoice: Invoice): List<InvoiceItem> = find {
            InvoiceItemTable.invoiceId eq invoice.id
        }.toList()

        fun findByIdAndInvoice(id: UUID, invoice: Invoice): InvoiceItem? = find {
            (InvoiceItemTable.id eq id) and (InvoiceItemTable.invoiceId eq invoice.id)
        }.firstOrNull()
    }
}
