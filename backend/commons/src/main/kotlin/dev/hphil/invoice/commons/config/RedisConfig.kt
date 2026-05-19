package dev.hphil.invoice.commons.config

import dev.hphil.invoice.commons.util.RedisStore
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.lettuce.core.RedisClient


fun Application.configureRedis() {
    val redisHost = environment.config.propertyOrNull("redis.host")?.getString() ?: "localhost"
    val redisPort = environment.config.propertyOrNull("redis.port")?.getString()?.toInt() ?: 6379

    val redisClient = RedisClient.create("redis://$redisHost:$redisPort")
    val connection = redisClient.connect()
    val commands = connection.async()
    val redisStore = RedisStore(commands)

    dependencies {
        provide { redisStore } cleanup {
            connection.close()
            redisClient.shutdown()
        }
    }
}