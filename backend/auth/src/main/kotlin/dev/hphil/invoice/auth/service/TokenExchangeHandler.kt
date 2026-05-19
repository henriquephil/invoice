package dev.hphil.invoice.auth.service

import dev.hphil.invoice.auth.exception.AuthenticationFailedException
import dev.hphil.invoice.auth.flow.ClientCredentialsGrantFlow
import dev.hphil.invoice.auth.flow.PasswordGrantFlow
import dev.hphil.invoice.auth.flow.RefreshTokenGrantFlow
import dev.hphil.invoice.auth.support.DeviceInfo
import dev.hphil.invoice.auth.support.deviceInfo
import dev.hphil.invoice.commons.dtos.auth.TokenRequest
import dev.hphil.invoice.commons.dtos.auth.TokenResponse
import dev.hphil.invoice.commons.util.action
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.application

fun Route.tokenRoute() {
    val tokenIssuer: TokenIssuer by application.dependencies
    val tokenExchangeHandler = TokenExchangeHandler(tokenIssuer)
    action("token") {
        tokenExchangeHandler.handle(receive(), deviceInfo)
    }
}

class TokenExchangeHandler(
    tokenIssuer: TokenIssuer
) {
    private val passwordFlow = PasswordGrantFlow(tokenIssuer)
    private val refreshTokenFlow = RefreshTokenGrantFlow(tokenIssuer)
    private val clientCredentialsFlow = ClientCredentialsGrantFlow(tokenIssuer)

    suspend fun handle(request: TokenRequest, deviceInfo: DeviceInfo): TokenResponse {
        val config = ClientConfig.get(request.clientId, request.clientSecret)
        return when (request.grantType) {
            "password" -> passwordFlow.exchange(request, deviceInfo, config.findGrantConfig())
            "refresh_token" -> refreshTokenFlow.exchange(request, deviceInfo, config.findGrantConfig())
            "client_credentials" -> clientCredentialsFlow.exchange(deviceInfo, config.findGrantConfig())
            else -> throw AuthenticationFailedException("Unsupported grant type: ${request.grantType}")
        }
    }
}

