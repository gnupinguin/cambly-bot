package io.gnupinguin.camblybot.endpoint

import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping
class GreetingPage {

    @GetMapping(value = [""], produces = [MediaType.TEXT_HTML_VALUE])
    fun index(): String {
        return "index.html"
    }

}