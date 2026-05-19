package dev.hphil.invoice.account.database

import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.time.OffsetDateTime
import java.util.UUID

class Account(id: EntityID<UUID>) : UUIDEntity(id) {
    var ownerUserId by AccountTable.ownerUserId
    var name by AccountTable.name
    var document by AccountTable.document
    var email by AccountTable.email
    var phone by AccountTable.phone
    var createdAt by AccountTable.createdAt

    companion object : UUIDEntityClass<Account>(AccountTable) {
        fun new(ownerUserId: UUID, name: String, document: String, email: String, phone: String) = new {
            this.ownerUserId = ownerUserId
            this.name = name
            this.document = document
            this.email = email
            this.phone = phone
            createdAt = OffsetDateTime.now()
        }

        fun findByOwnerUserId(ownerUserId: UUID) = find {
            AccountTable.ownerUserId eq ownerUserId
        }.toList()

        fun findByIdAndOwnerUserId(id: UUID, ownerUserId: UUID) = find {
            (AccountTable.id eq id) and (AccountTable.ownerUserId eq ownerUserId)
        }.firstOrNull()
    }
}
