package dev.hphil.invoice.gateway.auth

import dev.hphil.invoice.commons.dtos.auth.TokenResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*


fun Route.authRoutes() {
    val authService: AuthService by application.dependencies
    post("/register") {
        val tokenResponse = authService.register(call.receive())
        call.handleAuthenticated(tokenResponse)
    }
    post("/login") {
        val tokenResponse = authService.login(call.receive())
        call.handleAuthenticated(tokenResponse)
    }
    post("/logout") {
        // todo authHttpClient.revoke
        call.sessions.clear<TokenResponse>()
        call.respond(HttpStatusCode.NoContent)
    }
}

private suspend fun ApplicationCall.handleAuthenticated(tokenResponse: TokenResponse) {
    sessions.set(tokenResponse)
    respond(HttpStatusCode.NoContent)
}
