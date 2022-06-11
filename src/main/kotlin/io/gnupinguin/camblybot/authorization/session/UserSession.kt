package io.gnupinguin.camblybot.authorization.session

import java.util.*

data class UserSession(val chatId: Long, val issueTime: Date, val expirationTime: Date)