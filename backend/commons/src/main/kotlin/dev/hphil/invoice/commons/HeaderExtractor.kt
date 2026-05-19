package dev.hphil.invoice.commons

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import dev.hphil.invoice.commons.dtos.account.AccountResponse
import dev.hphil.invoice.commons.http.client.AccountHttpClient
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*
import io.ktor.util.logging.*
import java.util.*
import java.util.concurrent.TimeUnit

const val HEADER_ACCOUNT_ID = "X-Account-Id"
val AccountKey = AttributeKey<AccountResponse>("account")
val AccountIdExtractor = createApplicationPlugin(name = "AccountIdExtractor") {
    val accountClient: AccountHttpClient by application.dependencies
    val cache: Cache<AccountCacheKey, AccountResponse> = Caffeine.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .maximumSize(1_000)
        .build()
    onCall { call ->
        val accountId = call.request.header(HEADER_ACCOUNT_ID)
            ?.takeIf { it.isNotBlank() }
            ?: return@onCall
        val userId = call.request.header(HEADER_USER_ID)!!
        val cacheKey = AccountCacheKey(userId, accountId)
        val accountResponse = try {
            cache.getIfPresent(cacheKey) ?: accountClient
                .getAccount(UUID.fromString(accountId))
                .also { cache.put(cacheKey, it) }
        } catch (e: Exception) {
            return@onCall call.respond(HttpStatusCode.Forbidden, "Invalid or unauthorized '$HEADER_ACCOUNT_ID'")
        }
        call.attributes.put(AccountKey, accountResponse)
    }
}
val ApplicationCall.account get() = attributes[AccountKey]
data class AccountCacheKey(val userId: String, val accountId: String)


const val HEADER_USER_ID = "X-User-Id"
val UserIdKey = AttributeKey<UUID>("userId")
val UserIdExtractor = createApplicationPlugin(name = "UserIdExtractor") {
    val logger = KtorSimpleLogger("UserIdExtractor")
    onCall { call ->
        val userIdHeader = call.request.header(HEADER_USER_ID)
        userIdHeader
            ?.takeIf { it.isNotBlank() }
            ?.let { userId ->
                try {
                    call.attributes.put(UserIdKey, UUID.fromString(userId))
                } catch (e: Exception) {
                    logger.error("Failed to parse userId '$userId': ${e.message}")
                }
            }
    }
}
val ApplicationCall.userId get() = attributes[UserIdKey]
