package io.gnupinguin.camblybot.authorization.session

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

interface UserSessionValidator {

    fun validate(userSession: UserSession?): Boolean

}

@Component
class UserSessionValidatorImpl: UserSessionValidator {

    companion object {
        @JvmStatic
        private val log = LoggerFactory.getLogger(UserSessionValidatorImpl::class.java)
    }

    override fun validate(userSession: UserSession?): Boolean {
        if (userSession != null) {
            return Date().before(userSession.expirationTime)
        }
        log.info("JWT is expired")
        return false
    }

}