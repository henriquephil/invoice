package dev.hphil.invoice.gateway.session

import dev.hphil.invoice.commons.dtos.auth.TokenResponse
import dev.hphil.invoice.commons.util.CustomJson
import dev.hphil.invoice.commons.util.RedisStore
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.sessions.SameSite
import io.ktor.server.sessions.SessionStorage
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.sameSite
import io.ktor.server.sessions.serialization.KotlinxSessionSerializer
import io.ktor.util.logging.KtorSimpleLogger

private val log = KtorSimpleLogger("dev.hphil.invoice.gateway.session.Session")

const val SESSION_COOKIE_NAME = "X-Session-Id"

fun Application.configureSession() {
    val redis: RedisStore by dependencies
    install(Sessions) {
        cookie<TokenResponse>(SESSION_COOKIE_NAME, RedisSessionCookie(redis)) {
            cookie.path = "/"
            cookie.httpOnly = true
            cookie.sameSite = SameSite.Strict
            // cookie.secure = true // prod (HTTPS)
            serializer = KotlinxSessionSerializer(CustomJson)
        }
    }
}

private class RedisSessionCookie(private val redis: RedisStore) : SessionStorage {

    override suspend fun write(id: String, value: String) {
        log.info("Writing session for id: $id")
        redis.set(SessionKeys.session(id), 60 * 60 * 24, value)
    }

    override suspend fun read(id: String): String {
        val key = SessionKeys.session(id)
        log.info("Reading session for id: $id")
        return redis.get(key) ?: run {
            log.warn("No session found for key $key in Redis")
            throw NoSuchElementException("No key $key found on redis")
        }
    }

    override suspend fun invalidate(id: String) {
        log.info("Invalidating session for id: $id")
        redis.delete(SessionKeys.session(id))
    }
}