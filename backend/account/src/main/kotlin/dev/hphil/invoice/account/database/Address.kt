package dev.hphil.invoice.account.database

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class Address(id: EntityID<UUID>) : UUIDEntity(id) {
    var accountId by AddressTable.accountId
    var street by AddressTable.street
    var number by AddressTable.number
    var complement by AddressTable.complement
    var neighborhood by AddressTable.neighborhood
    var city by AddressTable.city
    var state by AddressTable.state
    var zipCode by AddressTable.zipCode
    var country by AddressTable.country

    companion object : UUIDEntityClass<Address>(AddressTable) {
        fun new(
            account: Account,
            street: String = "",
            number: String = "",
            complement: String = "",
            neighborhood: String = "",
            city: String = "",
            state: String = "",
            zipCode: String = "",
            country: String = ""
        ) = new {
            this.accountId = account.id
            this.street = street
            this.number = number
            this.complement = complement
            this.neighborhood = neighborhood
            this.city = city
            this.state = state
            this.zipCode = zipCode
            this.country = country
        }

        fun findByAccount(account: Account) = find {
            AddressTable.accountId eq account.id.value
        }.first()
    }
}

