package dev.hphil.invoice.gateway.proxy

import dev.hphil.invoice.commons.HEADER_ACCOUNT_ID
import dev.hphil.invoice.commons.HEADER_USER_ID
import dev.hphil.invoice.commons.config.ServiceToken
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.di.*
import io.ktor.server.plugins.origin
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.ktor.v3_0.KtorClientTelemetry

private val PROPAGATED_HEADERS = setOf(
    HttpHeaders.ContentType,
    HttpHeaders.ContentEncoding,
    HttpHeaders.Accept,
    HttpHeaders.AcceptLanguage,
    HttpHeaders.AcceptEncoding,
    HttpHeaders.UserAgent,
    HEADER_ACCOUNT_ID
).map { it.lowercase() }

fun Route.proxyRoutes() {
    val openTelemetry: OpenTelemetry by application.dependencies
    val serviceToken: ServiceToken by application.dependencies
    val serviceRegistry: ServiceRegistry by application.dependencies

    val httpClient = HttpClient(CIO) {
        engine {
            maxConnectionsCount = 1000
            endpoint {
                maxConnectionsPerRoute = 100
                keepAliveTime = 5000
                connectTimeout = 2000
                requestTimeout = 30000
            }
        }
        install(KtorClientTelemetry) {
            setOpenTelemetry(openTelemetry)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 2000
            socketTimeoutMillis = 30000
        }
        install(serviceToken.clientPlugin)
        followRedirects = false
    }

    suspend fun ApplicationCall.handleProxy() {
        val service = parameters["service"] ?: return respond(HttpStatusCode.BadRequest)
        val path = parameters.getAll("path")?.joinToString("/") ?: ""

        Span.current().updateName("${request.httpMethod.value} $service/$path")

        val baseUrl = serviceRegistry[service] ?: return respond(
            HttpStatusCode.NotFound,
            "Service $service not mapped"
        )

        val targetUrl = "${baseUrl.removeSuffix("/")}/${path.removePrefix("/")}"

        val principal = principal<UserIdPrincipal>()
        val proxiedResponse = httpClient.request(targetUrl) {
            method = request.httpMethod
            headers {
                request.headers.forEach { key, values ->
                    if (key.lowercase() in PROPAGATED_HEADERS) {
                        appendAll(key, values)
                    }
                }
            }
            header(HEADER_USER_ID, principal?.name)
            header(HttpHeaders.XForwardedFor, request.buildForwardedFor())
            setBody(receiveChannel())
        }

        respond(object : OutgoingContent.WriteChannelContent() {
            override val contentLength: Long? = proxiedResponse.headers[HttpHeaders.ContentLength]?.toLong()
            override val contentType: ContentType? =
                proxiedResponse.headers[HttpHeaders.ContentType]?.let { ContentType.parse(it) }
            override val status: HttpStatusCode = proxiedResponse.status
            override suspend fun writeTo(channel: ByteWriteChannel) {
                proxiedResponse.bodyAsChannel().copyAndClose(channel)
            }
        })
    }

    authenticate("session") {
        route("/{service}/{path...}") {
            handle {
                call.handleProxy()
            }
        }
    }
    route("/public/{service}/{path...}") {
        handle {
            call.handleProxy()
        }
    }
}

private fun ApplicationRequest.buildForwardedFor(): String {
    val clientIp = origin.remoteHost
    val existingForwardedFor = headers[HttpHeaders.XForwardedFor]
    return if (existingForwardedFor != null) "$existingForwardedFor, $clientIp" else clientIp
}
