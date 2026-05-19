package dev.hphil.invoice.catalog.database

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.core.java.javaUUID

object ItemTable : UUIDTable("items") {
    val accountId = javaUUID("account_id").index("index_items_account_id")
    val type = enumerationByName<ItemType>("type", 10)
    val name = text("name")
    val measureUnit = text("measure_unit")
    val unitPrice = decimal("unit_price", 20, 2)
    val currency = text("currency")

}
