package dev.hphil.invoice.auth.support

import io.ktor.http.HttpHeaders
import io.ktor.server.plugins.origin
import io.ktor.server.request.header
import io.ktor.server.routing.RoutingCall

data class DeviceInfo(
    val userAgent: String,
    val deviceId: String,
    val ip: String,
)

val RoutingCall.deviceInfo get() = DeviceInfo(
    request.header(HttpHeaders.UserAgent) ?: "",
    request.header("X-Device-ID") ?: "",
    request.origin.remoteHost
)