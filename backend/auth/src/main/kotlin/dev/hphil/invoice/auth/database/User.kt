package dev.hphil.invoice.auth.database

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.time.OffsetDateTime
import java.util.UUID

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    var username by UserTable.username
    var password by UserTable.password
    var name by UserTable.name
    var createdAt by UserTable.createdAt

    companion object : UUIDEntityClass<User>(UserTable) {
        fun new(username: String, password: String, name: String) = new {
            this.username = username
            this.password = password
            this.name = name
            createdAt = OffsetDateTime.now()
        }

        fun findByUsername(username: String) = find {
            UserTable.username eq username
        }.firstOrNull()
    }

}
