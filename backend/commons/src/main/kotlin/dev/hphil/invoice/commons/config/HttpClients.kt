package dev.hphil.invoice.commons.config

import dev.hphil.invoice.commons.http.client.AccountHttpClient
import dev.hphil.invoice.commons.http.client.AuthHttpClient
import dev.hphil.invoice.commons.http.client.CatalogHttpClient
import dev.hphil.invoice.commons.util.CustomJson
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.instrumentation.ktor.v3_0.KtorClientTelemetry


fun Application.configureHttpClientFactory() {
    configureHeaderPropagation() // todo this looks like shit being here
    val openTelemetry: OpenTelemetry by dependencies
    val headerPropagation: HeaderPropagation by dependencies
    val factory = object : HttpClientFactory {
        override fun newClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(CustomJson)
                }
                install(KtorClientTelemetry) {
                    setOpenTelemetry(openTelemetry)
                }
                install(headerPropagation.clientPlugin)

                expectSuccess = true
                HttpResponseValidator {
                    handleResponseExceptionWithRequest { exception, _ ->
                        val clientException = exception as? ResponseException ?: return@handleResponseExceptionWithRequest
                        throw HttpRequestException(clientException.response.status, clientException.response.bodyAsText())
                    }
                }

//            install(Logging) {
//                logger = Logger.DEFAULT
//                level = LogLevel.HEADERS
//            }
                block()
            }
            return client
        }
    }
    dependencies {
        provide { factory }
    }
}
interface HttpClientFactory {
    fun newClient(block: HttpClientConfig<*>.() -> Unit = {}): HttpClient
}

fun Application.configureBackendHttpClients() {
    log.info("Configuring http clients")
    val httpClientFactory: HttpClientFactory by dependencies
    val serviceToken: ServiceToken by dependencies
    val servicesConfig = environment.config.config("services")
    val backendClient = httpClientFactory.newClient {
        install(serviceToken.clientPlugin)
    }
    dependencies {
        provide { AuthHttpClient(backendClient, servicesConfig.property("auth.baseUrl").getString()) }
        provide { AccountHttpClient(backendClient, servicesConfig.property("account.baseUrl").getString()) }
        provide { CatalogHttpClient(backendClient, servicesConfig.property("catalog.baseUrl").getString()) }
    }
}

class HttpRequestException(
    val statusCode: HttpStatusCode,
    message: String
) : RuntimeException("HTTP request failed with status code $statusCode and message: $message")