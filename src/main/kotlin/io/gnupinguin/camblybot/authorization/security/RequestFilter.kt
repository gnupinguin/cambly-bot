package io.gnupinguin.camblybot.authorization.security

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Service
class RequestFilter: Filter {

    companion object {
        @JvmStatic
        private val log = LoggerFactory.getLogger(RequestFilter::class.java)
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val url: String = (request as HttpServletRequest).requestURL.toString()
        val queryString: String? = request.queryString
        log.info("Request: $url$queryString")
        chain.doFilter(request, response)
    }

}