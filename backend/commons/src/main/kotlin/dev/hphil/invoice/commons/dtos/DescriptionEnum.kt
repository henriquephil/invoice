package dev.hphil.invoice.commons.dtos

import kotlinx.serialization.Serializable

@Serializable
data class DescriptionEnum(
    val enum: String,
    val description: String
)