package dev.hphil.invoice.gateway.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class UserRegisterRequest(
    val email: String,
    val password: String,
    val name: String
)
