package dev.hphil.invoice.commons.dtos.auth

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class TokenResponse(
    val accessToken: String,
    @Contextual
    val expiresAt: OffsetDateTime,
    val refreshToken: String? = null,
    @Contextual
    val refreshExpiresAt: OffsetDateTime? = null
)
