package io.gnupinguin.camblybot.endpoint

import io.gnupinguin.camblybot.authorization.SessionContextProvider
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class EmailEndpoint(private val sessionContextProvider: SessionContextProvider) {

    companion object {
        @JvmStatic
        private val log = LoggerFactory.getLogger(EmailEndpoint::class.java)
    }

    @GetMapping("/user")
    fun user(): User {
        return User(sessionContextProvider.getContext().accessToken)
    }

    data class User(val name: String)

}