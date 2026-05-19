package dev.hphil.invoice.auth.flow

import at.favre.lib.crypto.bcrypt.BCrypt
import dev.hphil.invoice.auth.service.TokenIssuer
import dev.hphil.invoice.auth.service.AccessRefreshTokenGrantConfig
import dev.hphil.invoice.auth.database.User
import dev.hphil.invoice.auth.exception.AuthenticationFailedException
import dev.hphil.invoice.auth.support.DeviceInfo
import dev.hphil.invoice.commons.dtos.auth.TokenRequest
import dev.hphil.invoice.commons.dtos.auth.TokenResponse
import dev.hphil.invoice.commons.util.txRead
import dev.hphil.invoice.commons.util.txWrite
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PasswordGrantFlow(
    private val tokenIssuer: TokenIssuer
) {

    private val log = KtorSimpleLogger(this::class.simpleName!!)
    private val verifier = BCrypt.verifyer()

    suspend fun exchange(request: TokenRequest, deviceInfo: DeviceInfo, grantConfig: AccessRefreshTokenGrantConfig): TokenResponse {
        val username = requireNotNull(request.username) { "username is required for PASSWORD grant type" }
        val password = requireNotNull(request.password) { "password is required for PASSWORD grant type" }

        val user = txRead { User.findByUsername(username) }
            ?.takeIf { user ->
                withContext(Dispatchers.Default) {
                    verifier.verify(password.toCharArray(), user.password)
                }.verified.also {
                    if (!it) {
                        log.warn("user ${user.id.value} entered an invalid password")
                    }
                }
            }
            ?: throw AuthenticationFailedException("User not found or invalid credentials")
        return txWrite { tokenIssuer.forUser(user, deviceInfo, grantConfig) }
    }
}
