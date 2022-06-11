package io.gnupinguin.camblybot.authorization.security

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

interface JwtProvider {

    fun encode(claims: JWTClaimsSet): String

    fun decode(jwt: String): JWTClaimsSet?

}

@Service
class JwtProviderImpl(private val signer: RSASSASigner,
                      private val verifier: RSASSAVerifier,
                      private val configuration: SecurityConfiguration): JwtProvider {
    companion object {
        @JvmStatic
        private val log = LoggerFactory.getLogger(JwtProviderImpl::class.java)
    }

    override fun encode(claims: JWTClaimsSet): String {
        val signedJWT = SignedJWT(header(), claims)
        signedJWT.sign(signer)
        return signedJWT.serialize()
    }

    private fun header() = JWSHeader.Builder(JWSAlgorithm.RS256).keyID(configuration.rsaJwkKey.keyID).build()

    override fun decode(jwt: String): JWTClaimsSet? {
        try {
            val signedJwt = SignedJWT.parse(jwt)
            if (signedJwt.verify(verifier)) {
                return signedJwt.jwtClaimsSet
            }
            log.info("JWT verification was failed")
        } catch (e: Exception) {
            log.info("Invalid JWT: {}", jwt, e)
        }
        return null
    }

}