package io.gnupinguin.camblybot.service

import io.gnupinguin.camblybot.clients.Message
import io.gnupinguin.camblybot.persistance.Lesson
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

interface CamblyEmailParser {

    fun parse(messages: List<Message>): List<Lesson>

}

@Component
class CamblyEmailParserImpl: CamblyEmailParser {

    companion object {
        @JvmStatic
        private val log = LoggerFactory.getLogger(CamblyEmailParserImpl::class.java)

        @JvmStatic
        private val chatRegex = Regex(
            "Lesson with (?<teacher>.+?)\\s+\r\n" +
                    "(?<date>.+?\\((?<timezone>.*?)\\))" +
                    ", duration (?<duration>\\d+) minutes.*?" +
                    "(?:Chat Summary:\\s+(?<chat>.*?)\r\n\r\n\r\n)?", setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL))
    }

    override fun parse(messages: List<Message>): List<Lesson> {
        return messages
            .map { message ->
                message.payload?.parts
                    ?.filter { it.mimeType == "text/plain" }
                    ?.mapNotNull { it.body.data }
                    ?.mapNotNull { data ->
                        val content = decodeMessageContent(data)
                        val matchResult = chatRegex.find(content)
                        if (matchResult != null) {
                            parseLesson(matchResult)
                        } else {
                            log.info("Could not parse data: {}", data)
                            null
                        }
                    }
            }.flatMap { it.orEmpty() }
    }

    private fun parseLesson(matchResult: MatchResult): Lesson {
        val (teacher, date, timezone, duration, chat) = matchResult.destructured
        val dateFormatter = SimpleDateFormat("EEEE, MMM dd, yyyy, h:ma")
        dateFormatter.timeZone = TimeZone.getTimeZone(timezone)
        return Lesson(
            teacher = teacher,
            date = dateFormatter.parse(date),
            duration = duration.toInt(),
            words = parseWords(chat, teacher)
        )
    }

    private fun parseWords(chat: String, teacher: String): List<String> {
        val teacherPrefix = "$teacher: "
        return chat.split(Regex("\r\n"))
            .filter { it.startsWith(teacherPrefix) }
            .map { it.substring(teacherPrefix.length).trim() }
    }

    private fun decodeMessageContent(data: String): String {
        val bytes = Base64.getUrlDecoder().decode(data)
        return String(bytes, Charsets.UTF_8)
    }

}