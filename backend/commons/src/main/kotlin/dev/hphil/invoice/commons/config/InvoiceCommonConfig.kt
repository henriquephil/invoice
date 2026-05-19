package dev.hphil.invoice.commons.config

import com.auth0.jwk.JwkProviderBuilder
import dev.hphil.invoice.commons.dtos.HealthResponse
import dev.hphil.invoice.commons.util.CustomJson
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.csrf.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureCommonPlugins() {
    configureTracing()
    configureCORS()
//    configureCSRF()
    configureHttpSerialization()
    configureHealthEndpoint()
// logs, observability, metrics, circuit breaker, /health
}

fun Application.configureHealthEndpoint() {
    routing {
        get("/health") {
            call.respond(HttpStatusCode.OK, HealthResponse())
        }
    }
}


fun Application.configureCORS() {
    log.info("Configuring CORS")
    install(CORS) {
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
//        allowHost("localhost:3000")

        anyMethod()

        allowHeaders { true } // @TODO: configure correct headers
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)

        allowOrigins { true }

        allowCredentials = true
    }
}

fun Application.configureHttpSerialization() {
    log.info("Configuring serialization")
    install(ContentNegotiation) {
        json(CustomJson)
    }
}

private fun Application.configureCSRF() {
    install(CSRF) {
        allowOrigin("http://localhost:8080")
        originMatchesHost()
        checkHeader("X-CSRF-Token")
    }
}

fun Application.configureSecurity() {
    log.info("Configuring service security")
    val authDomain = environment.config.property("services.auth.baseUrl").getString()
    authentication {
        jwt {
            realm = "hphil.invoice"
            verifier(JwkProviderBuilder(authDomain).build(), AUTH_ISSUER)
            validate { credential -> JWTPrincipal(credential.payload) }
        }
    }
}
