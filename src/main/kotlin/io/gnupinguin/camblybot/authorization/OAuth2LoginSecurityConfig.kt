package io.gnupinguin.camblybot.authorization

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

@EnableWebSecurity
class OAuth2LoginSecurityConfig: WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests { a ->
            a.antMatchers("/", "/error", "/index.js").permitAll()
                .anyRequest().authenticated().and()
        }.csrf { c ->
            c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        }.logout { l ->
            l.logoutSuccessUrl("/").permitAll()
        }.oauth2Login()
            .defaultSuccessUrl("/success", true)
    }

}