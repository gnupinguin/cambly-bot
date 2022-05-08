package io.gnupinguin.camblybot.authorization

import org.springframework.stereotype.Component
import java.util.*

interface UserCodeGenerator {

    fun generate(): String

}

@Component
class UserCodeGeneratorImpl: UserCodeGenerator {

    override fun generate() = UUID.randomUUID().toString().replace("-","")

}