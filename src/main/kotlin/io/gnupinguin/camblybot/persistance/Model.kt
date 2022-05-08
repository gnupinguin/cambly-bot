package io.gnupinguin.camblybot.persistance

import java.util.*

//TODO support id
data class Lesson(val teacher: String, val date: Date, val duration: Int, val words: List<String>)

data class User(val telegramId: Long, val accessToken: String)