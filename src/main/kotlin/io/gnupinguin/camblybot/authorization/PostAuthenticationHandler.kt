package io.gnupinguin.camblybot.authorization

import io.gnupinguin.camblybot.authorization.session.UserSession
import io.gnupinguin.camblybot.authorization.session.UserSessionProvider
import io.gnupinguin.camblybot.bot.BotApp
import io.gnupinguin.camblybot.persistance.User
import io.gnupinguin.camblybot.persistance.UserRepository
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class PostAuthenticationHandler(private val repository: UserRepository,
                                private val userSessionProvider: UserSessionProvider,
                                private val clientService: OAuth2AuthorizedClientService,
                                private val bot: BotApp) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        if (isOAuthToken(authentication)) {
            val oauthToken = authentication as OAuth2AuthenticationToken
            val session = getUserSession(request)
            if (session != null) {
                val client: OAuth2AuthorizedClient = clientService.loadAuthorizedClient(oauthToken.authorizedClientRegistrationId, oauthToken.name)
                repository.putUser(createUser(session, client, oauthToken))
                bot.successAuthorize(session.chatId)
                clientService.removeAuthorizedClient(oauthToken.authorizedClientRegistrationId, oauthToken.principal.name)
            }
        }
    }

    private fun createUser(
        session: UserSession,
        client: OAuth2AuthorizedClient,
        oauthToken: OAuth2AuthenticationToken
    ) = User(session.chatId, client.accessToken.tokenValue, getEmail(oauthToken))

    private fun isOAuthToken(authentication: Authentication) =
        authentication.javaClass.isAssignableFrom(OAuth2AuthenticationToken::class.java)

    private fun getUserSession(request: HttpServletRequest): UserSession? {
        val jwtParam = request.parameterMap["state"]
        val jwt = if (!jwtParam.isNullOrEmpty()) jwtParam.first() else null
        return userSessionProvider.parse(jwt)
    }

    private fun getEmail(oauthToken: OAuth2AuthenticationToken) =
        oauthToken.principal.attributes["email"] as String
}