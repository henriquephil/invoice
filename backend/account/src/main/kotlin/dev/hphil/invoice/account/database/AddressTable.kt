package dev.hphil.invoice.account.database

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.timestampWithTimeZone

object AddressTable : UUIDTable("addresses") {
    val accountId = reference("account_id", AccountTable, ReferenceOption.CASCADE)
    val street = text("street")
    val number = text("number")
    val complement = text("complement")
    val neighborhood = text("neighborhood")
    val city = text("city")
    val state = text("state")
    val zipCode = text("zip_code")
    val country = text("country")
}
