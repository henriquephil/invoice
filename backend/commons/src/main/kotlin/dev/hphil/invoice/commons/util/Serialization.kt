package dev.hphil.invoice.commons.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

val CustomJson = Json {
    serializersModule = SerializersModule {
        contextual(OffsetDateTime::class, OffsetDateTimeSerializer)
        contextual(UUID::class, UUIDSerializer)
        contextual(BigDecimal::class, BigDecimalSerializer)
    }
    ignoreUnknownKeys = true
    coerceInputValues = true
    encodeDefaults = true
}