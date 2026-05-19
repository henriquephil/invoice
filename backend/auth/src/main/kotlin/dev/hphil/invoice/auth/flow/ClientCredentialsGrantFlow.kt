package dev.hphil.invoice.auth.flow

import dev.hphil.invoice.auth.service.TokenIssuer
import dev.hphil.invoice.auth.service.ClientCredentialsGrantConfig
import dev.hphil.invoice.auth.support.DeviceInfo
import dev.hphil.invoice.commons.dtos.auth.TokenResponse

class ClientCredentialsGrantFlow(
    private val tokenIssuer: TokenIssuer
) {

    fun exchange(deviceInfo: DeviceInfo, grantConfig: ClientCredentialsGrantConfig): TokenResponse {
        return tokenIssuer.forClient(deviceInfo, grantConfig)
    }
}
