package io.gnupinguin.camblybot.authorization

import io.gnupinguin.camblybot.authorization.session.UserSessionProvider
import io.gnupinguin.camblybot.authorization.session.UserSessionValidator
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest

import javax.servlet.http.HttpServletRequest


class JwtStateAuthorizationRequestResolver(
    registrationRepository: ClientRegistrationRepository?,
    authorizationRequestBaseUri: String,
    private val userSessionValidator: UserSessionValidator,
    private val userSessionProvider: UserSessionProvider) : OAuth2AuthorizationRequestResolver {
    private val defaultResolver: OAuth2AuthorizationRequestResolver

    init {
        defaultResolver = DefaultOAuth2AuthorizationRequestResolver(registrationRepository, authorizationRequestBaseUri)
    }

    override fun resolve(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        val authRequest = defaultResolver.resolve(request)
        return customizeAuthorizationRequest(authRequest, request)
    }

    override fun resolve(request: HttpServletRequest, clientRegistrationId: String?): OAuth2AuthorizationRequest? {
        val authRequest = defaultResolver.resolve(request, clientRegistrationId)
        return customizeAuthorizationRequest(authRequest, request)
    }

    private fun customizeAuthorizationRequest(
        authRequest: OAuth2AuthorizationRequest?,
        httpRequest: HttpServletRequest): OAuth2AuthorizationRequest? {
        if (authRequest != null) {
            val jwt = getJwtRequestParameter(httpRequest)
            val userSession = userSessionProvider.parse(jwt)
            if (userSessionValidator.validate(userSession)) {
                return OAuth2AuthorizationRequest.from(authRequest)
                    .state(jwt)
                    .build()
            }
            throw RuntimeException()
        }
        return null
    }

    private fun getJwtRequestParameter(httpRequest: HttpServletRequest): String? {
        val jwtParam = httpRequest.parameterMap["jwt"]
        if (!jwtParam.isNullOrEmpty()) {
            return jwtParam.first()
        }
        return null
    }

}