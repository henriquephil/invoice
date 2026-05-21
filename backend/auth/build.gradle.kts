plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Ktor server dependencies
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)

    implementation(project(":commons"))
    implementation(libs.opentelemetry.sdk.extension.autoconfigure)
    implementation(libs.opentelemetry.semconv)
    implementation(libs.opentelemetry.exporter.otlp)
    implementation(libs.opentelemetry.ktor)
    implementation(libs.postgresql)
    implementation(libs.h2)
    implementation(libs.ktor.server.csrf)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.di)
    implementation(libs.ktor.server.kafka)
    implementation(libs.ktor.client.core)
    implementation(libs.aws.sdk.ssm)
    implementation(libs.aws.sdk.regions)
    implementation(libs.aws.sdk.auth)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.java.time)
    implementation(libs.exposed.jdbc)
    implementation(libs.flyway)
    implementation(libs.flyway.postgres)
    implementation(libs.ktor.server.metrics.micrometer)
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.nimbus.jose.jwt)
    implementation(libs.hikariCP)
    implementation(libs.bcrypt)
}
