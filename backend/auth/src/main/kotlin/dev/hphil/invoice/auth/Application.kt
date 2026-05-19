package dev.hphil.invoice.auth

import dev.hphil.invoice.auth.service.configureJwt
import dev.hphil.invoice.auth.service.registerRoute
import dev.hphil.invoice.auth.service.tokenRoute
import dev.hphil.invoice.commons.config.configureCommonPlugins
import dev.hphil.invoice.commons.config.configureDatabase
import dev.hphil.invoice.commons.config.configureObservability
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureObservability()
    configureDatabase()
    configureJwt()
    configureCommonPlugins()
    routing {
        registerRoute()
        tokenRoute()
    }
}

