package dev.hphil.invoice.gateway.session

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.hphil.invoice.commons.config.AUTH_ISSUER
import dev.hphil.invoice.commons.dtos.auth.TokenResponse
import dev.hphil.invoice.commons.http.client.AuthHttpClient
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.di.*
import io.ktor.server.response.*
import io.ktor.util.logging.*
import java.util.concurrent.TimeUnit

private val authLogger = KtorSimpleLogger("dev.hphil.invoice.gateway.session.Authentication")

fun Application.configureSessionUserAuthentication() {
    val authHttpClient: AuthHttpClient by dependencies
    val userTokenVerifier = UserTokenVerifier(authHttpClient)
    authentication {
        session<TokenResponse>("session") {
            validate { token ->
                authLogger.info("Validating session token")
                userTokenVerifier.verify(token)
            }
            challenge {
                authLogger.warn("Session validation failed, responding with Unauthorized.")
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
    authLogger.info("Session user authentication configured")
}

class UserTokenVerifier(
    authHttpClient: AuthHttpClient
) {
    private val authJwkProvider = JwkProviderBuilder(authHttpClient.baseUrl)
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()
    private val authKeyProvider = JwksKeyProvider(authJwkProvider)
    private val authJwtVerifier = JWT.require(Algorithm.RSA256(authKeyProvider))
        .withIssuer(AUTH_ISSUER)
        .build()

    fun verify(token: TokenResponse): UserIdPrincipal? {
        authLogger.info("Verifying access token")
        return runCatching {
            val decoded = authJwtVerifier.verify(token.accessToken)
            authLogger.info("Token verified successfully for subject: ${decoded.subject}")
            UserIdPrincipal(decoded.subject)
        }.getOrElse {
            authLogger.error("Token verification failed", it)
            null
        }
    }
}