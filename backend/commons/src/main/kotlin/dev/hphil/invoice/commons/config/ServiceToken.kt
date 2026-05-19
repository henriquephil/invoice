package dev.hphil.invoice.commons.config

import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.github.benmanes.caffeine.cache.Caffeine
import dev.hphil.invoice.commons.dtos.auth.TokenRequest
import dev.hphil.invoice.commons.dtos.auth.TokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit


fun Application.configureServiceToken() {
    log.info("Configuring Service token")
    val httpClientFactory: HttpClientFactory by dependencies
    val conf = environment.config

    val authServiceBaseUrl = conf.property("services.auth.baseUrl").getString()
    val clientId = conf.property("auth.service.clientId").getString()
    val clientSecret = conf.property("auth.service.clientSecret").getString()
    val httpClient = httpClientFactory.newClient()

    val serviceToken = ServiceToken(httpClient, authServiceBaseUrl, clientId, clientSecret)
    dependencies {
        provide { serviceToken }
    }
}

@OptIn(DelicateCoroutinesApi::class)
class ServiceToken(
    private val httpClient: HttpClient,
    private val authServiceBaseUrl: String,
    private val clientId: String,
    private val clientSecret: String
) {
    private val log = KtorSimpleLogger(this::class.simpleName!!)

    private data class TokenData(
        val accessToken: String,
        val expiresAt: OffsetDateTime
    )

    private val tokenCache: AsyncLoadingCache<String, TokenData> = Caffeine.newBuilder()
        .refreshAfterWrite(57, TimeUnit.MINUTES) // todo build based on token expiration time
        .expireAfterWrite(60, TimeUnit.MINUTES)
        .buildAsync { _, _ ->
            GlobalScope.future {
                log.info("Fetching new service token")
                val request = TokenRequest(
                    grantType = "client_credentials",
                    clientId,
                    clientSecret
                )
                val response = httpClient.post(authServiceBaseUrl) {
                    url { appendPathSegments("token") }
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }.body<TokenResponse>()
                TokenData(response.accessToken, response.expiresAt)
            }
        }

    suspend fun getToken(): String {
        val serviceToken = tokenCache.get("service-token").await()
        if (serviceToken.expiresAt.isBefore(OffsetDateTime.now()))
            println("token expired")
        return serviceToken.accessToken
    }

    val clientPlugin = createClientPlugin("ServiceToken") {
        onRequest { request, _ ->
            request.bearerAuth(getToken())
        }
    }
}
