package dev.hphil.invoice.gateway.auth

import dev.hphil.invoice.commons.dtos.auth.RegisterRequest
import dev.hphil.invoice.commons.dtos.auth.TokenRequest
import dev.hphil.invoice.commons.dtos.auth.TokenResponse
import dev.hphil.invoice.commons.http.client.AuthHttpClient
import io.ktor.server.application.Application
import io.ktor.server.config.*
import io.ktor.server.plugins.di.dependencies

fun Application.configureAuthService() {
    val authHttpClient: AuthHttpClient by dependencies
    val config = environment.config
    dependencies {
        provide { AuthService(authHttpClient, config) }
    }
}

class AuthService(
    private val authHttpClient: AuthHttpClient,
    conf: ApplicationConfig
) {
    private val clientId = conf.property("auth.user.clientId").getString()
    private val clientSecret = conf.property("auth.user.clientSecret").getString()

    suspend fun register(request: UserRegisterRequest): TokenResponse =
        authHttpClient.register(
            RegisterRequest(
                clientId = clientId,
                clientSecret = clientSecret,
                username = request.email,
                password = request.password,
                name = request.name
            )
        )

    suspend fun login(request: UserLoginRequest): TokenResponse =
        authHttpClient.token(
            TokenRequest(
                grantType = "password",
                clientId = clientId,
                clientSecret = clientSecret,
                username = request.email,
                password = request.password
            )
        )

    suspend fun refresh(refreshToken: String): TokenResponse =
        authHttpClient.token(
            TokenRequest(
                grantType = "refresh_token",
                clientId = clientId,
                clientSecret = clientSecret,
                refreshToken = refreshToken
            )
        )
}