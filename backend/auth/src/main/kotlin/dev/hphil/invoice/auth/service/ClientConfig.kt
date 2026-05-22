package dev.hphil.invoice.auth.service

import dev.hphil.invoice.auth.exception.AuthenticationFailedException

private val clientConfigMap = listOf(
    ClientConfig(
        clientId = "user",
        clientSecret = "dev-secret-user",
        registerable = true,
        grantConfigs = listOf(
            AccessRefreshTokenGrantConfig(
                accessTokenExpirationMinutes = 15L,
                refreshTokenValidityDays = 30L
            )
        )
    ),
    ClientConfig(
        clientId = "service",
        clientSecret = "dev-secret-service",
        grantConfigs = listOf(
            ClientCredentialsGrantConfig(
                accessTokenExpirationMinutes = 60L
            )
        )
    )
).associateBy { it.clientId }

class ClientConfig(
    val clientId: String,
    val clientSecret: String,
    val registerable: Boolean = false,
    val grantConfigs: List<GrantConfig>
) {
    inline fun <reified T : GrantConfig> findGrantConfig(): T = grantConfigs.filterIsInstance<T>().firstOrNull()
        ?: throw AuthenticationFailedException("Client does not support grant type")

    companion object {
        fun get(clientId: String, clientSecret: String): ClientConfig {
            val config = clientConfigMap[clientId]
                ?: throw AuthenticationFailedException("Invalid client_id: $clientId")
            if (clientSecret != config.clientSecret) {
                throw AuthenticationFailedException("Invalid client secret")
            }
            return config
        }
    }
}

interface GrantConfig

class AccessRefreshTokenGrantConfig(
    val accessTokenExpirationMinutes: Long,
    val refreshTokenValidityDays: Long
) : GrantConfig

class ClientCredentialsGrantConfig(
    val accessTokenExpirationMinutes: Long
) : GrantConfig
