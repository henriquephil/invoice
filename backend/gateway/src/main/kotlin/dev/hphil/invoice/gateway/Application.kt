package dev.hphil.invoice.gateway

import dev.hphil.invoice.commons.config.*
import dev.hphil.invoice.gateway.auth.*
import dev.hphil.invoice.gateway.proxy.configureServiceRegistry
import dev.hphil.invoice.gateway.proxy.proxyRoutes
import dev.hphil.invoice.gateway.session.configureSession
import dev.hphil.invoice.gateway.session.configureSessionUserAuthentication
import dev.hphil.invoice.gateway.session.configureUserTokenRefresher
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureObservability()
    configureRedis()
    configureHttpClientFactory()
    configureServiceToken()
    configureBackendHttpClients()
    configureCommonPlugins()

    configureAuthService()
    configureServiceRegistry()

    configureSession() // 1
    configureUserTokenRefresher() // 2
    configureSessionUserAuthentication() // 3

    routing {
        route("api") {
            authRoutes()
            proxyRoutes()
        }
    }
}
