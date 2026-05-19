package dev.hphil.invoice.invoice

import dev.hphil.invoice.commons.AccountIdExtractor
import dev.hphil.invoice.commons.account
import dev.hphil.invoice.commons.config.configureCommonPlugins
import dev.hphil.invoice.commons.config.configureDatabase
import dev.hphil.invoice.commons.config.configureBackendHttpClients
import dev.hphil.invoice.commons.config.configureHttpClientFactory
import dev.hphil.invoice.commons.config.configureServiceToken
import dev.hphil.invoice.commons.config.configureSecurity
import dev.hphil.invoice.commons.config.configureObservability
import dev.hphil.invoice.commons.util.*
import dev.hphil.invoice.invoice.handler.InvoiceHandler
import dev.hphil.invoice.invoice.handler.InvoiceItemHandler
import dev.hphil.invoice.invoice.handler.IssueHandler
import dev.hphil.invoice.invoice.handler.SettingsHandler
import dev.hphil.invoice.invoice.service.InvoiceService
import dev.hphil.invoice.invoice.service.SettingsService
import dev.hphil.invoice.invoice.service.SnapshotService
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
        provide(::InvoiceService)
        provide(::SettingsService)
        provide(::SnapshotService)
        provide(::IssueHandler)
        provide(::InvoiceHandler)
        provide(::InvoiceItemHandler)
        provide(::SettingsHandler)
    }
    configureSecurity()
    configureCommonPlugins()
    configureRoutes()
}

private fun Application.configureRoutes() {
    routing {
        authenticate {
            install(AccountIdExtractor)
            invoiceRoutes()
            settingsRoutes()
        }
    }
}

private fun Route.invoiceRoutes() {
    val invoiceHandler: InvoiceHandler by application.dependencies
    val invoiceItemHandler: InvoiceItemHandler by application.dependencies
    val issueHandler: IssueHandler by application.dependencies
    route("invoices") {
        create {
            invoiceHandler.create(account)
        }
        find {
            invoiceHandler.list(account)
        }
        routeParam<UUID>("invoiceId") { invoiceId ->
            find {
                invoiceHandler.find(invoiceId(), account)
            }
            update {
                invoiceHandler.update(invoiceId(), receive(), account)
            }
            del {
                invoiceHandler.delete(invoiceId(), account)
            }
            action("issue") {
                issueHandler.issue(invoiceId(), account)
            }
            route("items") {
                create {
                    invoiceItemHandler.create(invoiceId(), receive(), account)
                }
                routeParam<UUID>("invoiceItemId") { invoiceItemId ->
                    update {
                        invoiceItemHandler.update(invoiceId(), invoiceItemId(), receive(), account)
                    }
                    del {
                        invoiceItemHandler.delete(invoiceId(), invoiceItemId(), account)
                    }
                }
            }
        }
    }
}

private fun Route.settingsRoutes() {
    val settingsHandler: SettingsHandler by application.dependencies
    route("settings") {
        find {
            settingsHandler.getByAccount(account)
        }
        update {
            settingsHandler.updateForAccount(account, receive())
        }
    }
}
