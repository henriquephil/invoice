package dev.hphil.invoice.commons.config

import io.ktor.client.plugins.api.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.dependencies
import io.ktor.util.logging.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import org.apache.kafka.common.header.Headers
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

fun Application.configureHeaderPropagation() {
    intercept(ApplicationCallPipeline.Setup) {
        withContext(ContextHeaders(call)) {
            proceed()
        }
    }
    val headerPropagation = HeaderPropagation()
    dependencies {
        provide { headerPropagation }
    }
}

class HeaderPropagation {
    val clientPlugin = createClientPlugin("HeaderPropagation") {
        onRequest { request, _ ->
            currentCoroutineContext()[ContextHeaders]?.headers?.forEach { (key, values) ->
                values.forEach { value ->
                    request.headers.append(key, value)
                }
            }
        }
    }
}

private class ContextHeaders(val headers: Map<String, List<String>>) : AbstractCoroutineContextElement(Key) {
    companion object Key : CoroutineContext.Key<ContextHeaders>

    constructor(call: ApplicationCall) : this(
        INTERNAL_PROPAGATION_HEADERS.mapNotNull { key ->
            call.request.headers.getAll(key)?.let { value -> key to value }
        }.toMap()
    )
    constructor(kafkaHeaders: Headers) : this(kafkaHeaders
        .groupBy({ it.key().lowercase() }, { String(it.value()) })
        .filterKeys { it in INTERNAL_PROPAGATION_HEADERS }
    )
}


private val INTERNAL_PROPAGATION_HEADERS = setOf(
    // Common tracing headers (e.g., for OpenTelemetry, Jaeger, Zipkin)
    "traceparent",
    "tracestate",
    "x-request-id",
    "x-trace-id",
    "x-b3-traceid",
    "x-b3-spanid",
    "x-b3-parentspanid",
    "x-b3-sampled",
    "x-b3-flags",
    // custom headers
    "x-account-id",
    "x-user-id",
    "x-forwarded-for"
)
