package io.gnupinguin.camblybot.endpoint

import io.gnupinguin.camblybot.authorization.AuthorizationStorage
import io.gnupinguin.camblybot.authorization.SessionContextProvider
import io.gnupinguin.camblybot.bot.BotApp
import io.gnupinguin.camblybot.persistance.User
import io.gnupinguin.camblybot.persistance.UserRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping
class GreetingPage(private val sessionContextProvider: SessionContextProvider,
                   private val authorizationStorage: AuthorizationStorage,
                   private val userRepository: UserRepository,
                   private val bot: BotApp) {

    @GetMapping(value = [""], produces = [MediaType.TEXT_HTML_VALUE])
    fun index(): String {
        return "index.html"
    }

    //TODO point fo DDoS. Can be extracted to TokenEndpoint or continue using InMemoryUserCodeStorage
    @GetMapping(value = ["success"], produces = [MediaType.TEXT_HTML_VALUE])
    @PostMapping(value = ["success"], produces = [MediaType.TEXT_HTML_VALUE])
    fun successLogin(@CookieValue("USER-CODE") userCode: String?): String {
        if(userCode != null) {
            val chatId = authorizationStorage.getChatId(userCode)
            if (chatId != null) {
                userRepository.putUser(User(chatId, sessionContextProvider.getContext().accessToken))
                bot.successAuthorize(chatId)
                return "redirect:https://t.me/CamblyBot"
            }
        }

        return "redirect:http://localhost:8080?error=Error"
    }

}