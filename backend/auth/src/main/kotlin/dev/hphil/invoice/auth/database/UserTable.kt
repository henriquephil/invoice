package dev.hphil.invoice.auth.database

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.javatime.timestampWithTimeZone

object UserTable : UUIDTable("users") {
    val username = text("username").uniqueIndex("unique_users_username")
    val password = text("password")
    val name = text("name")
    val createdAt = timestampWithTimeZone("created_at")
}
