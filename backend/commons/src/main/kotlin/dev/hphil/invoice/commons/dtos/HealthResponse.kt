package dev.hphil.invoice.commons.dtos

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String = "UP"
)
