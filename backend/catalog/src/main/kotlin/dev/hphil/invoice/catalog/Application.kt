package dev.hphil.invoice.catalog

import dev.hphil.invoice.commons.AccountIdExtractor
import dev.hphil.invoice.commons.account
import dev.hphil.invoice.commons.config.configureCommonPlugins
import dev.hphil.invoice.commons.config.configureDatabase
import dev.hphil.invoice.commons.config.configureBackendHttpClients
import dev.hphil.invoice.commons.config.configureHttpClientFactory
import dev.hphil.invoice.commons.config.configureServiceToken
import dev.hphil.invoice.commons.config.configureSecurity
import dev.hphil.invoice.commons.config.configureObservability
import dev.hphil.invoice.commons.util.create
import dev.hphil.invoice.commons.util.find
import dev.hphil.invoice.commons.util.routeParam
import dev.hphil.invoice.commons.util.update
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.di.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import java.util.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureObservability()
    configureDatabase()
    configureHttpClientFactory()
    configureServiceToken()
    configureBackendHttpClients()
    dependencies {
        provide(::ItemService)
        provide(::CustomerService)
    }
    configureSecurity()
    configureCommonPlugins()
    configureRoutes()
}

private fun Application.configureRoutes() {
    routing {
        authenticate {
            install(AccountIdExtractor)
            itemsRoutes()
            customersRoutes()
        }
    }
}

private fun Route.itemsRoutes() {
    val itemService: ItemService by application.dependencies
    route("items") {
        create {
            itemService.create(receive(), account)
        }
        find {
            itemService.getAll(account, parameters["filter"])
        }
        routeParam<UUID>("id") { id ->
            find {
                itemService.get(id(), account)
            }
            update {
                itemService.update(id(), receive(), account)
            }
        }
    }
}

private fun Route.customersRoutes() {
    val customerService: CustomerService by application.dependencies
    route("customers") {
        create {
            customerService.create(receive(), account)
        }
        find {
            customerService.getAll(account)
        }
        routeParam<UUID>("id") { id ->
            find {
                customerService.get(id(), account)
            }
            update {
                customerService.update(id(), receive(), account)
            }
        }
    }
}