package dev.hphil.invoice.auth.database

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.javatime.timestampWithTimeZone

object AuthenticationTokenTable : UUIDTable("authentication_tokens") {
    val userId = reference("user_id", UserTable)
    val refreshToken = text("refresh_token").uniqueIndex("unique_authentication_tokens_refresh_token")
    val refreshTokenExpiration = timestampWithTimeZone("refresh_token_expiration")

    val deviceName = text("device_name")
    val deviceId = text("device_id")
    val ipAddress = text("ip_address")

    val revoked = bool("revoked")
    val createdAt = timestampWithTimeZone("created_at")
}
