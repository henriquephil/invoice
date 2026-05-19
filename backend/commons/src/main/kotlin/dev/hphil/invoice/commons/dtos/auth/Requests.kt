package dev.hphil.invoice.commons.dtos.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val clientId: String,
    val clientSecret: String,
    val username: String,
    val password: String,
    val name: String
)

@Serializable
data class TokenRequest(
    val grantType: String,
    val clientId: String,
    val clientSecret: String,
    val username: String? = null,
    val password: String? = null,
    val refreshToken: String? = null
)
