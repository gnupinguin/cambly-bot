package io.gnupinguin.camblybot.authorization.security

import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.RSAKey
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component


@ConstructorBinding
@ConfigurationProperties("security")
data class SecurityConfiguration(val rsaJwkKey: RSAKey, val ttl: Int)

@Component
@ConfigurationPropertiesBinding
class RsaKeyConverter: Converter<String, RSAKey> {
    override fun convert(source: String): RSAKey? = RSAKey.parse(source)
}

@org.springframework.context.annotation.Configuration
class Configuration {

    @Bean
    fun signer(configuration: SecurityConfiguration): RSASSASigner = RSASSASigner(configuration.rsaJwkKey)

    @Bean
    fun verifier(configuration: SecurityConfiguration): RSASSAVerifier = RSASSAVerifier(configuration.rsaJwkKey)

}


