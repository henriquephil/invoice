package dev.hphil.invoice.gateway.session

import com.auth0.jwk.JwkProvider
import com.auth0.jwt.interfaces.RSAKeyProvider
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

class JwksKeyProvider(private val jwkProvider: JwkProvider) : RSAKeyProvider {
    override fun getPublicKeyById(keyId: String): RSAPublicKey {
        return jwkProvider.get(keyId).publicKey as RSAPublicKey
    }

    override fun getPrivateKey(): RSAPrivateKey? = null
    override fun getPrivateKeyId(): String? = null
}