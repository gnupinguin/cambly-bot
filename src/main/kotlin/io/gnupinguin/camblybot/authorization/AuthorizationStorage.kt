package io.gnupinguin.camblybot.authorization

import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

interface AuthorizationStorage {

    fun register(chatId: Long, code: String)

    fun getChatId(userCode: String): Long?

}

//TODO support time-evicting
@Repository
class InMemoryAuthorizationStorage: AuthorizationStorage {

    private val storage = ConcurrentHashMap<String, Long>()

    override fun register(chatId: Long, code: String) {
        storage[code] = chatId
    }

    override fun getChatId(userCode: String): Long? {
        return storage[userCode]
    }

}