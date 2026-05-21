plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    api(libs.ktor.client.core)
    api(libs.ktor.client.cio)
    api(libs.ktor.client.content.negotiation)
    api(libs.ktor.serialization.kotlinx.json)


    api(libs.ktor.server.core)
    api(libs.ktor.serialization.kotlinx.json)
    api(libs.ktor.server.content.negotiation)
    api(libs.ktor.server.call.logging)
    api(libs.ktor.server.call.id)
    api(libs.ktor.server.cors)
    api(libs.ktor.server.netty)
    api(libs.logback.classic)

    api(libs.opentelemetry.sdk.extension.autoconfigure)
    api(libs.opentelemetry.semconv)
    api(libs.opentelemetry.exporter.otlp)
    api(libs.opentelemetry.ktor)
    api(libs.postgresql)
    api(libs.h2)
    api(libs.ktor.server.csrf)
    api(libs.ktor.server.auth)
    api(libs.ktor.server.auth.jwt)
    api(libs.ktor.server.di)
    api(libs.ktor.server.kafka)
    api(libs.ktor.client.core)
    api(libs.aws.sdk.ssm)
    api(libs.aws.sdk.regions)
    api(libs.aws.sdk.auth)
    api(libs.exposed.core)
    api(libs.exposed.dao)
    api(libs.exposed.java.time)
    api(libs.exposed.jdbc)
    api(libs.flyway)
    api(libs.flyway.postgres)
    api(libs.ktor.server.metrics.micrometer)
    api(libs.micrometer.registry.prometheus)
    api(libs.nimbus.jose.jwt)
    api(libs.hikariCP)
    api(libs.bcrypt)
    api(libs.redis.lettuce)
    api(libs.rsql.parser)
    api(libs.caffeine)
    implementation("io.ktor:ktor-client-logging:3.4.0")
}
