package io.gnupinguin.camblybot.service

import io.gnupinguin.camblybot.clients.GmailClient
import io.gnupinguin.camblybot.clients.MailQuery
import io.gnupinguin.camblybot.persistance.Lesson
import io.gnupinguin.camblybot.persistance.User
import org.springframework.stereotype.Service
import java.util.*

@Service
class GmailLessonProvider(private val gmailClient: GmailClient,
                          private val emailParser: CamblyEmailParser): LessonProvider {

    override fun getCamblyLessonSummary(user: User, count: Int?): List<Lesson> {
        val authHeader = "Bearer ${user.accessToken}"
        val messages = gmailClient.getMessages(authorization = authHeader,
            q = MailQuery(from = "help@cambly.com", subject = "Lesson Summary"), maxResults = count)
        val extendedMessages = messages.messages.mapNotNull { message -> gmailClient.getMessage(authorization = authHeader, messageId = message.id) }
        return emailParser.parse(extendedMessages)
    }

    override fun getCamblyLessonSummary(user: User, count: Int?, before: Date): List<Lesson> {
        TODO("Not yet implemented")
    }
}