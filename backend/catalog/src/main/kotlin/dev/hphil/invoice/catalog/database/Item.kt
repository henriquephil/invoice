package dev.hphil.invoice.catalog.database

import dev.hphil.invoice.commons.util.ExposedRSQLVisitor
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.math.BigDecimal
import java.util.*

class Item(id: EntityID<UUID>) : UUIDEntity(id) {
    var accountId by ItemTable.accountId
    var type by ItemTable.type
    var name by ItemTable.name
    var measureUnit by ItemTable.measureUnit
    var unitPrice by ItemTable.unitPrice
    var currency by ItemTable.currency

    companion object : UUIDEntityClass<Item>(ItemTable) {
        fun new(accountId: UUID, type: ItemType, name: String, measureUnit: String, unitPrice: BigDecimal, currency: String) = new {
            this.accountId = accountId
            this.type = type
            this.name = name
            this.measureUnit = measureUnit
            this.unitPrice = unitPrice
            this.currency = currency
        }

        fun findByAccountId(accountId: UUID, filter: Op<Boolean>) = find {
            (ItemTable.accountId eq accountId) and filter
        }.toList()

        fun findByIdAndAccountId(id: UUID, accountId: UUID) = find {
            (ItemTable.id eq id) and (ItemTable.accountId eq accountId)
        }.firstOrNull()

        val filterDict = ExposedRSQLVisitor(
            mapOf<String, Column<*>>(
                "id" to ItemTable.id
            )
        )
    }
}
