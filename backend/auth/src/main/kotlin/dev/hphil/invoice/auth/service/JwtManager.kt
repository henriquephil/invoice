package dev.hphil.invoice.auth.service

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import dev.hphil.invoice.auth.database.AuthenticationToken
import dev.hphil.invoice.auth.database.Jwk
import dev.hphil.invoice.auth.database.JwkStatus
import dev.hphil.invoice.auth.database.User
import dev.hphil.invoice.auth.support.DeviceInfo
import dev.hphil.invoice.commons.config.AUTH_ISSUER
import dev.hphil.invoice.commons.dtos.auth.TokenResponse
import dev.hphil.invoice.commons.util.txRead
import dev.hphil.invoice.commons.util.txWrite
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*

fun Application.configureJwt() {
    val jwtManager = JwtManager()
    val tokenIssuer = TokenIssuer(jwtManager)
    dependencies {
        provide { tokenIssuer }
    }
    routing {
        get(".well-known/jwks.json") {
            val publicJwks = jwtManager.getPublicJwks().toJSONObject()
            call.respond(publicJwks)
        }
    }
}

class JwtManager {
    private val issuer: String = AUTH_ISSUER

    private val currentSigningKey: RSAKey by lazy {
        runBlocking {
            val dbKeys = txRead { Jwk.findValidKeys() }
            val currentKey = dbKeys.firstOrNull { it.status == JwkStatus.CURRENT } ?: generateNewKey()
            RSAKey.parse(currentKey.keyJson)
        }
    }

    private val publicJwkSet: JWKSet by lazy {
        runBlocking {
            val dbKeys = txRead { Jwk.findValidKeys() }
            JWKSet(dbKeys.map { JWK.parse(it.keyJson).toPublicJWK() })
        }
    }

    private suspend fun generateNewKey(): Jwk {
        val id = UUID.randomUUID()
        val rsa = RSAKeyGenerator(2048)
            .keyID(id.toString())
            .keyUse(KeyUse.SIGNATURE)
            .generate()
        return txWrite { Jwk.new(id, rsa.toJSONString()) }
    }

    fun createSignedJwt(
        sub: Any,
        expiresAt: Instant,
        vararg additionalClaims: Pair<String, Any>
    ): SignedJWT {
        val header = JWSHeader.Builder(JWSAlgorithm.RS256)
            .keyID(currentSigningKey.keyID)
            .type(JOSEObjectType.JWT)
            .build()
        val claimsSet = JWTClaimsSet.Builder()
            .issuer(issuer)
            .subject(sub.toString())
            .expirationTime(Date.from(expiresAt))
            .apply { additionalClaims.forEach { (k, v) -> claim(k, v) } }
            .build()
        return SignedJWT(header, claimsSet).apply {
            sign(RSASSASigner(currentSigningKey))
        }
    }

    fun getPublicJwks() = publicJwkSet
}


class TokenIssuer(
    private val jwtManager: JwtManager
) {
    private val log = KtorSimpleLogger(this::class.simpleName!!)

    fun forUser(user: User, deviceInfo: DeviceInfo, grantConfig: AccessRefreshTokenGrantConfig): TokenResponse {
        val accessTokenExpiration = OffsetDateTime.now().plusMinutes(grantConfig.accessTokenExpirationMinutes)
        val refreshTokenExpiration = OffsetDateTime.now().plusDays(grantConfig.refreshTokenValidityDays)

        val jwt = jwtManager.createSignedJwt(
            user.id.value,
            accessTokenExpiration.toInstant(),
            "email" to user.username,
            "name" to user.name
        )
        val authToken = AuthenticationToken.new(user, refreshTokenExpiration, deviceInfo)
        log.info("DEV - new token generated - refresh: ${authToken.refreshToken}")
        return TokenResponse(
            jwt.serialize(),
            accessTokenExpiration,
            authToken.refreshToken,
            authToken.refreshTokenExpiration
        )
    }

    fun forClient(deviceInfo: DeviceInfo, grantConfig: ClientCredentialsGrantConfig): TokenResponse {
        val accessTokenExpiration = OffsetDateTime.now().plusMinutes(grantConfig.accessTokenExpirationMinutes)

        val jwt = jwtManager.createSignedJwt(
            deviceInfo.deviceId,
            accessTokenExpiration.toInstant()
        )

        return TokenResponse(jwt.serialize(), accessTokenExpiration)
    }
}

