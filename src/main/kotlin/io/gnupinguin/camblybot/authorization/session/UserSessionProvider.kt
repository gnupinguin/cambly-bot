package io.gnupinguin.camblybot.authorization.session

import com.nimbusds.jwt.JWTClaimsSet
import io.gnupinguin.camblybot.authorization.security.JwtProvider
import io.gnupinguin.camblybot.authorization.security.SecurityConfiguration
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


interface UserSessionProvider {

    fun generate(chatId: Long): String

    fun parse(jwt: String?): UserSession?
}

@Component
class UserSessionProviderImpl(private val configuration: SecurityConfiguration,
                              private val jwtProvider: JwtProvider): UserSessionProvider {

    companion object {
        @JvmStatic
        private val log = LoggerFactory.getLogger(UserSessionProviderImpl::class.java)
    }

    override fun generate(chatId: Long): String {
        return jwtProvider.encode(claims(chatId))
    }

    override fun parse(jwt: String?): UserSession? {
        if (jwt != null) {
            val claims = jwtProvider.decode(jwt)
            if (claims != null) {
                return UserSession(claims.subject.toLong(), claims.issueTime, claims.expirationTime)
            }
        }
        return null
    }

    private fun claims(chatId: Long): JWTClaimsSet {
        val issueTime = LocalDateTime.now()
        return JWTClaimsSet.Builder()
            .jwtID(generateUUID())
            .issueTime(toDate(issueTime))
            .expirationTime(expirationDate(issueTime))
            .subject(chatId.toString())
            .build()
    }

    private fun generateUUID() = UUID.randomUUID().toString()

    private fun expirationDate(issueTime: LocalDateTime): Date {
        val expirationTime = issueTime.plusSeconds(configuration.ttl.toLong())
        return toDate(expirationTime)
    }

    private fun toDate(localDateTime: LocalDateTime) =
        Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())

}