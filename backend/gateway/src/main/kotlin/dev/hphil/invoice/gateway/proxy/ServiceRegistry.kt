package dev.hphil.invoice.gateway.proxy

import io.ktor.server.application.Application
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.getAs
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.plugins.di.provide

fun Application.configureServiceRegistry() {
    val servicesConfigMap: Map<String, Map<String, String>> = environment.config.property("services").getAs()
    val serviceRegistry = ServiceRegistry(servicesConfigMap)
    dependencies {
        provide { serviceRegistry }
    }
}

class ServiceRegistry(servicesConfigMap: Map<String, Map<String, String>>) {
    private val services: Map<String, String> = servicesConfigMap
        .mapValues { (key, value) ->
            value["baseUrl"] ?: throw IllegalArgumentException("Service $key is missing baseUrl configuration")
        }

    operator fun get(serviceName: String): String? = services[serviceName]
}