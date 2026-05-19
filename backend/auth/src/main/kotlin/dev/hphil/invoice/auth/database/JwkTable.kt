package dev.hphil.invoice.auth.database

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.javatime.timestampWithTimeZone

object JwkTable : UUIDTable("jwk") {
    val keyJson = text("key_json")
    val status = enumerationByName("status", 20, JwkStatus::class)

    val updatedAt = timestampWithTimeZone("updated_at")
    val createdAt = timestampWithTimeZone("created_at")
}
