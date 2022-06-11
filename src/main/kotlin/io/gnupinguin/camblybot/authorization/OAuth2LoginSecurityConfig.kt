package io.gnupinguin.camblybot.authorization

import io.gnupinguin.camblybot.authorization.session.UserSessionProvider
import io.gnupinguin.camblybot.authorization.session.UserSessionValidator
import io.gnupinguin.camblybot.bot.BotApp
import io.gnupinguin.camblybot.persistance.UserRepository
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

@EnableWebSecurity
class OAuth2LoginSecurityConfig(private val repo: ClientRegistrationRepository?,
                                private val userSessionValidator: UserSessionValidator,
                                private val userSessionProvider: UserSessionProvider,
                                private val userRepository: UserRepository,
                                private val clientService: OAuth2AuthorizedClientService,
                                private val bot: BotApp
): WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http {
            authorizeRequests {
                authorize("/", permitAll)
                authorize("/error", permitAll)
                authorize("/index.js", permitAll)
            }
            csrf {
                csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
            }
            logout {
                logoutSuccessUrl = "/"
                permitAll = true
            }
            oauth2Login {
                authorizationEndpoint {
                    authorizationRequestResolver = JwtStateAuthorizationRequestResolver(
                        repo, "/oauth2/authorization", userSessionValidator, userSessionProvider)
                }
                authenticationSuccessHandler = PostAuthenticationHandler(userRepository, userSessionProvider, clientService, bot)
            }
        }
    }

}