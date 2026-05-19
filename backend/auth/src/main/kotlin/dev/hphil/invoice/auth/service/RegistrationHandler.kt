package dev.hphil.invoice.auth.service

import at.favre.lib.crypto.bcrypt.BCrypt
import dev.hphil.invoice.auth.database.User
import dev.hphil.invoice.auth.exception.RegistrationFailedException
import dev.hphil.invoice.auth.support.DeviceInfo
import dev.hphil.invoice.auth.support.deviceInfo
import dev.hphil.invoice.commons.dtos.auth.RegisterRequest
import dev.hphil.invoice.commons.dtos.auth.TokenResponse
import dev.hphil.invoice.commons.util.action
import dev.hphil.invoice.commons.util.txWrite
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.registerRoute() {
    val tokenIssuer: TokenIssuer by application.dependencies
    val registrationHandler = RegistrationHandler(tokenIssuer)
    action("register") {
        registrationHandler.handle(receive(), deviceInfo)
    }
}

class RegistrationHandler(
    private val tokenIssuer: TokenIssuer
) {
    private val encoder = BCrypt.withDefaults()

    suspend fun handle(request: RegisterRequest, deviceInfo: DeviceInfo): TokenResponse {
        val config = ClientConfig.get(request.clientId, request.clientSecret)
        if (!config.registerable) {
            throw RegistrationFailedException("Client ${config.clientId} does not allow registration")
        }
        val grantConfig = config.findGrantConfig<AccessRefreshTokenGrantConfig>()
        val encodedPassword = withContext(Dispatchers.Default) {
            encoder.hashToString(12, request.password.toCharArray())
        }
        return txWrite {
            if (User.findByUsername(request.username) != null) {
                throw RegistrationFailedException("User with username ${request.username} already exists")
            }
            val user = User.new(request.username, encodedPassword, request.name)
            tokenIssuer.forUser(user, deviceInfo, grantConfig)
        }
    }
}

