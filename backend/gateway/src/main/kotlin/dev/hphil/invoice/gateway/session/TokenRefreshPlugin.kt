package dev.hphil.invoice.gateway.session

import dev.hphil.invoice.commons.config.HttpRequestException
import dev.hphil.invoice.commons.dtos.auth.TokenResponse
import dev.hphil.invoice.commons.util.RedisStore
import dev.hphil.invoice.commons.util.launchDetached
import dev.hphil.invoice.gateway.auth.AuthService
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.logging.*
import kotlinx.coroutines.delay
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random

private val log = KtorSimpleLogger("dev.hphil.invoice.gateway.session.TokenRefreshPlugin")

fun Application.configureUserTokenRefresher() {
    val redisStore: RedisStore by dependencies
    val authService: AuthService by dependencies
    val userTokenRefresher = UserTokenRefresher(redisStore, authService)
    val plugin = createApplicationPlugin(name = "TokenRefreshPlugin") {
        onCall { call ->
            with(userTokenRefresher) {
                call.refreshSessionIfNeeded()
            }
        }
    }
    log.info("TokenRefreshPlugin installed")
    install(plugin)
}

private class UserTokenRefresher(
    private val redisStore: RedisStore,
    private val authService: AuthService
) {

    companion object {
        private const val LOCK_TTL_SECONDS = 10L
        private const val LOCK_WAIT_MILLIS = 1000L
        private const val REFRESH_AHEAD_SECONDS = 60L
        private const val MAX_ATTEMPTS = 5
    }

    suspend fun ApplicationCall.refreshSessionIfNeeded(attempt: Int = 1) {
        val sessionId = sessionId ?: return
        val token = redisStore.getJson<TokenResponse>(SessionKeys.session(sessionId)) ?: return
        val secondsToExpire = ChronoUnit.SECONDS.between(OffsetDateTime.now(), token.expiresAt)
        log.info("Checking token for session $sessionId. Seconds to expire: $secondsToExpire")
        when {
            secondsToExpire <= 0 -> refreshNow(sessionId, token.refreshToken!!, attempt)
            secondsToExpire <= REFRESH_AHEAD_SECONDS -> refreshAhead(sessionId, token.refreshToken!!)
        }
    }

    private suspend fun ApplicationCall.refreshNow(sessionId: String, knownRefreshToken: String, attempt: Int) {
        log.info("Attempting to refresh and replace session $sessionId. Attempt $attempt of $MAX_ATTEMPTS")
        val result = doRefresh(sessionId, knownRefreshToken) {
            sessions.set(it)
        }
        when (result) {
            RefreshResult.Refreshed -> return
            RefreshResult.RefreshFailed -> {
                log.warn("Refresh token invalid or revoked for session $sessionId. Invalidating session.")
                sessions.clear<TokenResponse>()
                respond(Unauthorized)
            }
            RefreshResult.LockNotAcquired -> if (attempt <= MAX_ATTEMPTS) {
                val waitFor = LOCK_WAIT_MILLIS * attempt
                log.warn("Could not acquire lock for session $sessionId. Retrying in $waitFor ms.")
                delay(waitFor)
                verifyAndRetry(sessionId, knownRefreshToken, attempt)
            } else {
                log.warn("Exhausted all attempts to refresh session $sessionId. Giving up until next request.")
                respond(Unauthorized)
            }
        }
    }

    private suspend fun ApplicationCall.verifyAndRetry(sessionId: String, knownRefreshToken: String, attempt: Int) {
        val currentSessionInRedis = redisStore.getJson<TokenResponse>(SessionKeys.session(sessionId))
            ?: run {
                log.info("Session ended by another process for session $sessionId")
                sessions.clear<TokenResponse>()
                return respond(Unauthorized)
            }
        if (currentSessionInRedis.refreshToken == knownRefreshToken) {
            log.info("Token was not refreshed by another process for session $sessionId. Retrying refresh.")
            refreshNow(sessionId, knownRefreshToken, attempt + 1)
        } else {
            log.info("Token was refreshed by another process for session $sessionId. Updating session with new token.")
            sessions.set(currentSessionInRedis)
        }
    }

    private fun ApplicationCall.refreshAhead(sessionId: String, knownRefreshToken: String) {
        log.info("Launching background refresh for session $sessionId")
        launchDetached {
            doRefresh(sessionId, knownRefreshToken)
        }
    }

    private suspend fun doRefresh(sessionId: String, knownRefreshToken: String, updateToken: suspend (newToken: TokenResponse) -> Unit = {}): RefreshResult {
        val lockKey = SessionKeys.refreshLock(sessionId)
        val lockId = Random.nextLong().toString()
        log.info("Attempting to acquire refresh lock for session $sessionId with lock key $lockKey")
        if (!redisStore.setIfAbsent(lockKey, LOCK_TTL_SECONDS, lockId)) {
            log.warn("Failed to acquire refresh lock for session $sessionId. Another process may be refreshing.")
            return RefreshResult.LockNotAcquired
        }
        log.info("Acquired refresh lock for session $sessionId")
        try {
            // E must get it again to prevent race conditions
            val currentSessionInRedis = redisStore.getJson<TokenResponse>(SessionKeys.session(sessionId))
                ?: run {
                    log.info("Session ended by another process for session $sessionId")
                    return RefreshResult.RefreshFailed
                }
            val newToken = if (currentSessionInRedis.refreshToken != knownRefreshToken) {
                log.info("Token was already refreshed by another process for session $sessionId")
                currentSessionInRedis
            } else {
                log.info("Calling auth service to refresh token for session $sessionId")
                val newToken = authService.refresh(currentSessionInRedis.refreshToken!!)
                redisStore.setJson(SessionKeys.session(sessionId), 60 * 60 * 24, newToken)
                log.info("Successfully refreshed token for session $sessionId.")
                newToken
            }
            updateToken(newToken)
            return RefreshResult.Refreshed
        } catch (e: HttpRequestException) {
            log.error("Failed to refresh token for session $sessionId: ${e.message}")
            return RefreshResult.RefreshFailed
        } finally {
            if (redisStore.get(lockKey) == lockId) {
                log.info("Releasing refresh lock for key $lockKey")
                redisStore.delete(lockKey)
            }
        }
    }

    private sealed interface RefreshResult {
        data object Refreshed : RefreshResult
        data object LockNotAcquired : RefreshResult
        data object RefreshFailed: RefreshResult
    }
}