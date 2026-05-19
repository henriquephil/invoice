package dev.hphil.invoice.auth.database

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.time.OffsetDateTime
import java.util.*

class Jwk(id: EntityID<UUID>) : UUIDEntity(id) {
    var keyJson by JwkTable.keyJson
    var status by JwkTable.status
    var updatedAt by JwkTable.updatedAt
    var createdAt by JwkTable.createdAt

    companion object : UUIDEntityClass<Jwk>(JwkTable) {
        fun new(id: UUID, keyJson: String) = new(id) {
            this.keyJson = keyJson
            status = JwkStatus.CURRENT
            updatedAt = OffsetDateTime.now()
            createdAt = OffsetDateTime.now()
        }

        fun findValidKeys(): List<Jwk> = find {
            JwkTable.status neq JwkStatus.EXPIRED
        }.toList()

        fun findCurrent() = find {
            JwkTable.status eq JwkStatus.CURRENT
        }.firstOrNull()
    }
}
