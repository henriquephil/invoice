package dev.hphil.invoice.catalog.database

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.core.java.javaUUID

object CustomerTable : UUIDTable("customers") {
    val accountId = javaUUID("account_id").index("index_customers_account_id")
    val name = text("name")
    val document = text("document")
    val email = text("email")
    val phone = text("phone")
    val street = text("address_street")
    val number = text("address_number")
    val complement = text("address_complement")
    val neighborhood = text("address_neighborhood")
    val city = text("address_city")
    val state = text("address_state")
    val zipCode = text("address_zip_code")
    val country = text("address_country")
}
