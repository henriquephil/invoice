package dev.hphil.invoice.commons.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*
import io.opentelemetry.api.trace.Span
import org.slf4j.event.Level
import java.util.*

//fun Application.configureTracing() {
//    intercept(ApplicationCallPipeline.Plugins) {
//        val correlationId = call.request.headers[HttpHeaders.XCorrelationId] ?: UUID.randomUUID().toString()
//        MDC.put("correlationId", correlationId)
//        call.attributes.put(CORRELATION_ID_KEY, correlationId)
//        call.response.header(HttpHeaders.XCorrelationId, correlationId)
//
//        val requestId = UUID.randomUUID().toString()
//        MDC.put("requestId", requestId)
//        call.attributes.put(REQUEST_ID_KEY, requestId)
//        call.response.header(HttpHeaders.XRequestId, requestId)
//
//        try {
//            proceed()
//        } finally {
//            MDC.remove("correlationId")
//        }
//    }
//}

//object TracingKeys {
//    val CORRELATION_ID_KEY = AttributeKey<String>("CorrelationId")
//    val REQUEST_ID_KEY = AttributeKey<String>("RequestId")
//}

fun Application.configureTracing() {
    install(CallId) {
        generate { UUID.randomUUID().toString() }
        retrieveFromHeader(HttpHeaders.XRequestId)
        replyToHeader(HttpHeaders.XRequestId)
    }
//    intercept(ApplicationCallPipeline.Plugins) {
//        val correlationId = call.request.headers[HttpHeaders.XCorrelationId]
//            ?: UUID.randomUUID().toString()
//        call.attributes.put(CORRELATION_ID_KEY, correlationId)
//        call.response.header(HttpHeaders.XCorrelationId, correlationId)
//        proceed()
//    }
    install(CallLogging) {
        callIdMdc("requestId")
        mdc("traceId") {
            Span.current().spanContext.traceId
        }
        mdc("spanId") {
            Span.current().spanContext.spanId
        }
//        mdc("correlationId") { it.attributes[CORRELATION_ID_KEY] }
//        level = Level.TRACE
//        format { call ->
//            "${call.request.httpMethod.value} ${call.request.path()} -> ${call.response.status()}"
//        }
    }
}