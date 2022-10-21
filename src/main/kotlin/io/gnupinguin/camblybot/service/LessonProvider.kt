package io.gnupinguin.camblybot.service

import io.gnupinguin.camblybot.persistance.Lesson
import io.gnupinguin.camblybot.persistance.User
import java.util.*

interface LessonProvider {

    fun getCamblyLessonSummary(user: User, count: Int? = null): List<Lesson>

    fun getCamblyLessonSummary(user: User, count: Int? = null, before: Date): List<Lesson>

}