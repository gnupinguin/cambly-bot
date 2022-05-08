package io.gnupinguin.camblybot.service

import io.gnupinguin.camblybot.persistance.Lesson
import io.gnupinguin.camblybot.persistance.User

interface LessonProvider {

    fun getCamblyLessonSummary(user: User, count: Int? = null): List<Lesson>

}