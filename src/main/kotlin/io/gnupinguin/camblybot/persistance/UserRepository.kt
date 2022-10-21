package io.gnupinguin.camblybot.persistance

import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

interface UserRepository {

    fun getUser(telegramId: Long): User?

    fun putUser(user: User)

}

@Repository
class InMemoryUserRepository : UserRepository {

    private val users = ConcurrentHashMap<Long, User>()

    override fun getUser(telegramId: Long): User? {
        return users[telegramId]
    }

    override fun putUser(user: User) {
        users[user.telegramChatId] = user
    }

}