package io.gnupinguin.camblybot.bot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.network.Response
import com.github.kotlintelegrambot.network.fold
import io.gnupinguin.camblybot.authorization.AuthorizationStorage
import io.gnupinguin.camblybot.authorization.UserCodeGenerator
import io.gnupinguin.camblybot.persistance.UserRepository
import io.gnupinguin.camblybot.service.LessonProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class BotApp(private val configuration: BotConfiguration,
             private val userRepository: UserRepository,
             private val authRepository: AuthorizationStorage,
             private val userCodeGenerator: UserCodeGenerator,
             private val lessonProvider: LessonProvider) {

    companion object {
        @JvmStatic
        private val log = LoggerFactory.getLogger(BotApp::class.java)
    }

    private val bot = bot {
        token = configuration.token
        dispatch {
            command("authorize") {
                val user = userRepository.getUser(message.chat.id)
                if (user == null) {
                    val code = userCodeGenerator.generate()
                    authRepository.register(message.chat.id, code)
                    sendHtml("http://127.0.0.1:8080?userCode=$code")
                }
            }
            command("lastLesson") {
                val user = userRepository.getUser(message.chat.id)
                if (user == null) {
                    bot.sendMessage(chatId = chatId(), text = "Please, call /authorize first")
                } else {
                    lessonProvider.getCamblyLessonSummary(user, 1).forEach { lesson ->
                        val text = "Teacher: ${lesson.teacher} \n" +
                                "Date: ${lesson.date} \n" +
                                "Duration: ${lesson.duration}m \n" +
                                "Phrases: \n" +
                                lesson.words.map { "<b>$it</b>" }.joinToString(separator = "\n")
                        sendHtml(text)
                    }
                }
            }
        }
    }

    private fun CommandHandlerEnvironment.sendHtml(text: String, responseHandler: (Response<Message>?) -> Unit = {}) {
        bot.sendMessage(chatId = chatId(), text = text, ParseMode.HTML ).fold(responseHandler) {
            log.info("Exception during sending: {}", it.errorBody?.string(), it.exception)
        }
    }

    private fun CommandHandlerEnvironment.chatId() = ChatId.fromId(message.chat.id)

    fun successAuthorize(chatId: Long) {
        bot.sendMessage(chatId=ChatId.fromId(chatId), text="You are successfully authorized!")
    }

    @PostConstruct
    fun runBot() {
        bot.startPolling()
    }

}