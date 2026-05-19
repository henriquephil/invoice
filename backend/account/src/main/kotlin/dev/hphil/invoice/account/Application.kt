package dev.hphil.invoice.account

import dev.hphil.invoice.commons.UserIdExtractor
import dev.hphil.invoice.commons.config.configureCommonPlugins
import dev.hphil.invoice.commons.config.configureDatabase
import dev.hphil.invoice.commons.config.configureBackendHttpClients
import dev.hphil.invoice.commons.config.configureHttpClientFactory
import dev.hphil.invoice.commons.config.configureServiceToken
import dev.hphil.invoice.commons.config.configureSecurity
import dev.hphil.invoice.commons.config.configureObservability
import dev.hphil.invoice.commons.userId
import dev.hphil.invoice.commons.util.create
import dev.hphil.invoice.commons.util.del
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
        provide(::AccountService)
        provide(::AccountHandler)
        provide(::AddressHandler)
        provide(::BankingHandler)
    }
    configureSecurity()
    configureCommonPlugins()
    configureRoutes()
}

private fun Application.configureRoutes() {
    log.info("Configuring routes")
    val accountHandler: AccountHandler by dependencies
    val addressHandler: AddressHandler by dependencies
    val bankingHandler: BankingHandler by dependencies
    routing {
        authenticate {
            install(UserIdExtractor)
            create {
                accountHandler.create(receive(), userId)
            }
            find {
                accountHandler.getAll(userId)
            }
            routeParam<UUID>("accountId") { accountId ->
                find {
                    accountHandler.get(accountId(), userId)
                }
                update {
                    accountHandler.update(accountId(), receive(), userId)
                }
                route("address") {
                    find {
                        addressHandler.get(accountId(), userId)
                    }
                    update {
                        addressHandler.update(accountId(), receive(), userId)
                    }
                }
                route("banking") {
                    find {
                        bankingHandler.get(accountId(), userId)
                    }
                    update {
                        bankingHandler.update(accountId(), receive(), userId)
                    }
                    route("intermediary") {
                        update {
                            bankingHandler.updateIntermediary(accountId(), receive(), userId)
                        }
                        del {
                            bankingHandler.deleteIntermediary(accountId(), userId)
                        }
                    }
                }
            }
        }
    }
}
