package io.gnupinguin.camblybot.authorization

import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import org.junit.jupiter.api.Test

internal class UserSessionProviderImplTest {
    @Test
    fun `test rsa key generation`() {
        val rsaJWK: RSAKey = RSAKeyGenerator(2048)
            .keyID("0")
            .generate()
        val rsaPublicJWK: RSAKey = rsaJWK.toPublicJWK()
        println(rsaJWK.toJSONString())
    }

}