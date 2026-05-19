package dev.hphil.invoice.auth.database

import dev.hphil.invoice.auth.support.DeviceInfo
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.time.OffsetDateTime
import java.util.*

class AuthenticationToken(id: EntityID<UUID>) : UUIDEntity(id) {
    var userId by AuthenticationTokenTable.userId
    var refreshToken by AuthenticationTokenTable.refreshToken
    var refreshTokenExpiration by AuthenticationTokenTable.refreshTokenExpiration

    var deviceName by AuthenticationTokenTable.deviceName
    var deviceId by AuthenticationTokenTable.deviceId
    var ipAddress by AuthenticationTokenTable.ipAddress

    var revoked by AuthenticationTokenTable.revoked
    var createdAt by AuthenticationTokenTable.createdAt

    companion object : UUIDEntityClass<AuthenticationToken>(AuthenticationTokenTable) {
        fun new(user: User, refreshTokenExpiration: OffsetDateTime, deviceInfo: DeviceInfo) = new {
            this.userId = user.id
            this.refreshToken = UUID.randomUUID().toString()
            this.refreshTokenExpiration = refreshTokenExpiration

            this.deviceName = deviceInfo.userAgent
            this.deviceId = deviceInfo.deviceId
            this.ipAddress = deviceInfo.ip

            this.revoked = false
            this.createdAt = OffsetDateTime.now()
        }

        fun findByRefreshToken(refreshToken: String) = find {
            AuthenticationTokenTable.refreshToken eq refreshToken
        }.firstOrNull()

        fun revokeAllByUserId(userId: UUID) {
            find {
                (AuthenticationTokenTable.userId eq userId) and (AuthenticationTokenTable.revoked eq false)
            }.forEach {
                it.revoked = true
            }
        }
    }
}
