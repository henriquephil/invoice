package dev.hphil.invoice.commons.config

import io.ktor.server.application.*
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheus.*
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.instrumentation.ktor.v3_0.KtorServerTelemetry
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk
import io.opentelemetry.semconv.ServiceAttributes

fun Application.configureMicrometerPrometheus() {
    val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    install(MicrometerMetrics) {
        registry = prometheusRegistry
//        distributionStatisticConfig = DistributionStatisticConfig.Builder()
//            .percentilesHistogram(true)
//            .build()
    }
    routing {
        get("/metrics") {
            call.respond(prometheusRegistry.scrape())
        }
    }
}

fun Application.configureObservability() {
    val serviceName = environment.config.property("name").getString()
    val openTelemetry = getOpenTelemetry(serviceName)
    dependencies {
        provide{ openTelemetry }
    }
    install(KtorServerTelemetry) {
        setOpenTelemetry(openTelemetry)
//        spanNameExtractor {
//        }


//        capturedRequestHeaders(HttpHeaders.UserAgent)

//        spanKindExtractor {
//            if (httpMethod == HttpMethod.Post) {
//                SpanKind.PRODUCER
//            } else {
//                SpanKind.CLIENT
//            }
//        }

//        attributesExtractor {
//            onStart {
//                attributes.put("start-time", System.currentTimeMillis())
//            }
//            onEnd {
//                attributes.put("end-time", System.currentTimeMillis())
//            }
//        }
    }
}

private fun getOpenTelemetry(serviceName: String): OpenTelemetry {
//    System.setProperty("otel.metrics.exporter", "none") // todo why?
    return AutoConfiguredOpenTelemetrySdk.builder()
        .addResourceCustomizer { resource, _ ->
            resource.toBuilder()
                .put(ServiceAttributes.SERVICE_NAME, serviceName)
                .build()
        }
//        .addPropertiesCustomizer { mapOf(
//            "otel.metrics.exporter" to "none", // disable metrics
//
//            "otel.traces.exporter" to "zipkin", // zipkin traces
//            "otel.exporter.zipkin.endpoint" to "http://localhost:9411/api/v2/spans",
//
//            "otel.logs.exporter" to "otlp"
//        ) }
        .build()
        .openTelemetrySdk
}

