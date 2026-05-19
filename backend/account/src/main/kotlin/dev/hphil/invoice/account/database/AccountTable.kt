package dev.hphil.invoice.account.database

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.timestampWithTimeZone

object AccountTable : UUIDTable("accounts") {
    val ownerUserId = javaUUID("owner_user_id").index("index_accounts_owner_user_id")
    val name = text("name")
    val document = text("document")
    val email = text("email")
    val phone = text("phone")
    val createdAt = timestampWithTimeZone("created_at")
}
