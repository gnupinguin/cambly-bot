package io.gnupinguin.camblybot.authorization

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Component


data class SessionContext(val accessToken: String, val googleId: String)

@Component
class SessionContextProvider(private val clientService: OAuth2AuthorizedClientService) {

    fun getContext(): SessionContext {
        val client = getClient()
        return SessionContext(client.accessToken.tokenValue, client.principalName)
    }

    private fun getClient(): OAuth2AuthorizedClient {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication.javaClass.isAssignableFrom(OAuth2AuthenticationToken::class.java)) {
            val oauthToken = authentication as OAuth2AuthenticationToken
            val clientRegistrationId = oauthToken.authorizedClientRegistrationId
            if (clientRegistrationId == "google") {
                return clientService.loadAuthorizedClient(clientRegistrationId, oauthToken.name)
            }
        }
        throw RuntimeException("OAuth2AuthorizedClient not found")
    }

}