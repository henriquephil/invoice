package dev.hphil.invoice.catalog.database

import dev.hphil.invoice.commons.dtos.catalog.AddressDTO
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class Customer(id: EntityID<UUID>) : UUIDEntity(id) {
    var accountId by CustomerTable.accountId
    var name by CustomerTable.name
    var document by CustomerTable.document
    var email by CustomerTable.email
    var phone by CustomerTable.phone
    var street by CustomerTable.street
    var number by CustomerTable.number
    var complement by CustomerTable.complement
    var neighborhood by CustomerTable.neighborhood
    var city by CustomerTable.city
    var state by CustomerTable.state
    var zipCode by CustomerTable.zipCode
    var country by CustomerTable.country

    companion object : UUIDEntityClass<Customer>(CustomerTable) {
        fun new(
            accountId: UUID,
            name: String,
            document: String,
            email: String,
            phone: String,
            address: AddressDTO
        ) = new {
            this.accountId = accountId
            this.name = name
            this.document = document
            this.email = email
            this.phone = phone
            this.street = address.street
            this.number = address.number
            this.complement = address.complement
            this.neighborhood = address.neighborhood
            this.city = address.city
            this.state = address.state
            this.zipCode = address.zipCode
            this.country = address.country
        }

        fun findByAccountId(accountId: UUID) = find {
            CustomerTable.accountId eq accountId
        }.toList()

        fun findByIdAndAccountId(id: UUID, accountId: UUID) = find {
            (CustomerTable.id eq id) and (CustomerTable.accountId eq accountId)
        }.firstOrNull()
    }
}
