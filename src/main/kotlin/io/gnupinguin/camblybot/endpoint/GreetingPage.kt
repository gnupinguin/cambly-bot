package io.gnupinguin.camblybot.endpoint

import io.gnupinguin.camblybot.authorization.SessionContextProvider
import io.gnupinguin.camblybot.authorization.session.UserSessionProvider
import io.gnupinguin.camblybot.authorization.session.UserSessionValidator
import io.gnupinguin.camblybot.bot.BotApp
import io.gnupinguin.camblybot.persistance.UserRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping
class GreetingPage(private val sessionContextProvider: SessionContextProvider,
                   private val userRepository: UserRepository,
                   private val userSessionProvider: UserSessionProvider,
                   private val userSessionValidator: UserSessionValidator,
                   private val bot: BotApp) {

    @GetMapping(value = [""], produces = [MediaType.TEXT_HTML_VALUE])
    fun index(): String {
        return "index.html"
    }

}