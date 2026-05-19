package dev.hphil.invoice.commons.http.client

import dev.hphil.invoice.commons.dtos.auth.RegisterRequest
import dev.hphil.invoice.commons.dtos.auth.TokenRequest
import dev.hphil.invoice.commons.dtos.auth.TokenResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthHttpClient(private val httpClient: HttpClient, val baseUrl: String) {

    suspend fun token(request: TokenRequest): TokenResponse {
        return httpClient.post(baseUrl) {
            url { appendPathSegments("token") }
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun register(request: RegisterRequest): TokenResponse {
        return httpClient.post(baseUrl) {
            url { appendPathSegments("register") }
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}