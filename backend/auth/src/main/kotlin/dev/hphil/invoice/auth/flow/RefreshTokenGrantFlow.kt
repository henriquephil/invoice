package dev.hphil.invoice.auth.flow

import dev.hphil.invoice.auth.service.TokenIssuer
import dev.hphil.invoice.auth.service.AccessRefreshTokenGrantConfig
import dev.hphil.invoice.auth.database.AuthenticationToken
import dev.hphil.invoice.auth.database.User
import dev.hphil.invoice.auth.exception.AuthenticationFailedException
import dev.hphil.invoice.auth.support.DeviceInfo
import dev.hphil.invoice.commons.dtos.auth.TokenRequest
import dev.hphil.invoice.commons.dtos.auth.TokenResponse
import dev.hphil.invoice.commons.util.txWrite
import java.time.OffsetDateTime

class RefreshTokenGrantFlow(
    private val tokenIssuer: TokenIssuer
) {
    suspend fun exchange(request: TokenRequest, deviceInfo: DeviceInfo, grantConfig: AccessRefreshTokenGrantConfig): TokenResponse {
        val refreshTokenValue = requireNotNull(request.refreshToken)
        return txWrite {
            val authToken = AuthenticationToken.findByRefreshToken(refreshTokenValue)
                ?: throw AuthenticationFailedException("Invalid refresh token")

            if (authToken.revoked) {
                AuthenticationToken.revokeAllByUserId(authToken.userId.value)
                throw AuthenticationFailedException("Attempted to reuse a revoked refresh token")
            }

            authToken.revoked = true

            if (authToken.refreshTokenExpiration.isBefore(OffsetDateTime.now())) {
                throw AuthenticationFailedException("Expired refresh token")
            }

            val user = User.findById(authToken.userId)
                ?: throw AuthenticationFailedException("User not found for refresh token")

            tokenIssuer.forUser(user, deviceInfo, grantConfig)
        }
    }
}
