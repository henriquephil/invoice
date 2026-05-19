package dev.hphil.invoice.commons.util

import io.lettuce.core.SetArgs
import io.lettuce.core.api.async.RedisAsyncCommands
import kotlinx.coroutines.future.await

class RedisStore(val redis: RedisAsyncCommands<String, String>) {

    suspend inline fun get(key: String): String? = redis.get(key).await()

    suspend inline fun set(key: String, ttlSeconds: Long, value: String) {
        redis.setex(key, ttlSeconds, value).await()
    }

    suspend inline fun <reified T> getJson(key: String): T? = get(key)?.let { CustomJson.decodeFromString<T>(it) }

    suspend inline fun <reified T> setJson(key: String, ttlSeconds: Long, value: T) {
        set(key, ttlSeconds, CustomJson.encodeToString(value))
    }

    suspend fun setIfAbsent(key: String, ttlSeconds: Long, value: String): Boolean =
        redis.set(key, value, SetArgs().nx().ex(ttlSeconds)).await() == "OK"

    suspend fun delete(key: String) {
        redis.del(key).await()
    }
}
