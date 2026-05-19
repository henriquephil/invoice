package dev.hphil.invoice.auth

import dev.hphil.invoice.commons.dtos.auth.RegisterRequest
import dev.hphil.invoice.commons.dtos.auth.TokenRequest
import dev.hphil.invoice.commons.dtos.auth.TokenResponse
import dev.hphil.invoice.commons.util.CustomJson
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.apache.commons.lang3.RandomStringUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RoutingTest {
    @Test
    fun `full user flow - register, login, refresh`() = testApplication {
        environment { config = ApplicationConfig("application.conf") }
        val client = createClient { install(ContentNegotiation) { json(CustomJson) } }

        val email = RandomStringUtils.random(8, "abcdefghijklmnopqrstuvwxyz") + "@hphil.dev"
        val password = "password123"

        // 1. Register
        val registerRequest = RegisterRequest("user", email, password, "Test User")
        val registerResponse = client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(registerRequest)
        }
        assertEquals(HttpStatusCode.OK, registerResponse.status)
        val registerToken = registerResponse.body<TokenResponse>()
        assertNotNull(registerToken.accessToken)
        assertNotNull(registerToken.refreshToken)

        // 2. Login
        val loginRequest = TokenRequest(
            grantType = "password",
            clientId = "user",
            clientSecret = "dev-secret-user",
            username = email,
            password = password
        )
        val loginResponse = client.post("/token") {
            contentType(ContentType.Application.Json)
            setBody(loginRequest)
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)
        val loginToken = loginResponse.body<TokenResponse>()
        assertNotNull(loginToken.accessToken)
        assertNotNull(loginToken.refreshToken)

        // 3. Refresh
        val refreshToken = requireNotNull(loginToken.refreshToken)
        val refreshRequest = TokenRequest(
            grantType = "refresh_token",
            clientId = "user",
            clientSecret = "dev-secret-user",
            refreshToken = refreshToken
        )
        val refreshResponse = client.post("/token") {
            contentType(ContentType.Application.Json)
            setBody(refreshRequest)
        }
        assertEquals(HttpStatusCode.OK, refreshResponse.status)
        val refreshedToken = refreshResponse.body<TokenResponse>()
        assertNotNull(refreshedToken.accessToken)
        assertNotNull(refreshedToken.refreshToken)
    }

    @Test
    fun `client credentials flow`() = testApplication {
        environment { config = ApplicationConfig("application.conf") }
        val client = createClient { install(ContentNegotiation) { json(CustomJson) } }

        val request = TokenRequest(
            grantType = "client_credentials",
            clientId = "service",
            clientSecret = "dev-secret-service"
        )
        val response = client.post("/token") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val token = response.body<TokenResponse>()
        assertNotNull(token.accessToken)
        assertNull(token.refreshToken, "Client credentials flow should not return a refresh token")
    }

    @Test
    fun testLoginAndRefresh() = testApplication {
        val client = buildClient()
        val loginRequest = TokenRequest(
            grantType = "password",
            clientId = "user",
            clientSecret = "dev-secret-user",
            username = "test@test.com",
            password = "password"
        )
        val loginResponse = client.post("/token") {
            contentType(ContentType.Application.Json)
            setBody(loginRequest)
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)
        val tokenResponse = loginResponse.body<TokenResponse>()
        assertNotNull(tokenResponse.accessToken)
        assertNotNull(tokenResponse.refreshToken)

        val refreshRequest = TokenRequest(
            grantType = "refresh_token",
            clientId = "user",
            clientSecret = "dev-secret-user",
            refreshToken = tokenResponse.refreshToken
        )
        val refreshResponse = client.post("/token") {
            contentType(ContentType.Application.Json)
            setBody(refreshRequest)
        }
        assertEquals(HttpStatusCode.OK, refreshResponse.status)
        val refreshedTokenResponse = refreshResponse.body<TokenResponse>()
        assertNotNull(refreshedTokenResponse.accessToken)
        assertNotNull(refreshedTokenResponse.refreshToken)
    }

    @Test
    fun testClientCredentials() = testApplication {
        val client = buildClient()
        val request = TokenRequest(
            grantType = "client_credentials",
            clientId = "service",
            clientSecret = "dev-secret-service"
        )
        val response = client.post("/token") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val tokenResponse = response.body<TokenResponse>()
        assertNotNull(tokenResponse.accessToken)
        assertNull(tokenResponse.refreshToken)
    }
}