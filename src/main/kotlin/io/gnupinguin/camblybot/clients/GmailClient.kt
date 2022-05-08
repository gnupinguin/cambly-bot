package io.gnupinguin.camblybot.clients

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import java.util.*


@FeignClient(
    url = "https://gmail.googleapis.com/gmail/v1",
    name = "gmail",
    configuration = [GmailFeignClientConfiguration::class],
    decode404 = true
)
interface GmailClient {

    @GetMapping("/users/{userId}/messages")
    fun getMessages(@RequestHeader("Authorization") authorization: String,
                    @PathVariable("userId") userId: String = "me",
                    @RequestParam("maxResults") maxResults: Int? = 100,
                    @RequestParam("pageToken") pageToken: String? = null,
                    @RequestParam("q") q: MailQuery? = null): Messages

    @GetMapping("/users/{userId}/messages/{id}")
    fun getMessage(@RequestHeader("Authorization") authorization: String,
                   @PathVariable("userId") userId: String = "me",
                   @PathVariable("id") messageId: String,
                   @RequestParam("format") format: String = "full"): Message?

}

data class Message(val id: String,
                   val threadId: String,
                   val labelIds: List<String>?,
                   val historyId: String?,
                   val internalDate: Date?,
                   val sizeEstimate: Long?,
                   val raw: String?,
                   val payload: MessagePart?)

data class MessagePart(val partId: String,
                       val mimeType: String,
                       val filename: String,
                       val headers: List<MessageHeader>,
                       val parts: List<MessagePart>?,
                       val body: MessageBody)

class MessageBody(val attachmentId: String?,
                  val size: Int,
                  val data: String?)

data class MessageHeader(val name: String, val value: String)

data class Messages(val messages: List<Message>,
                    val nextPageToken: String?,
                    val resultSizeEstimate: Long)

//TODO support after date
data class MailQuery(val from: String, val subject: String) {
    override fun toString() = "from:$from${mapSubject()}"

    private fun mapSubject() = ifPresent("subject", subject) {s -> "($s)"}


    private fun ifPresent(field: String, value: Any?, map: (Any) -> String = {a -> "$a"}): String {
        return if (value != null) {
           " $field:${map(value)}"
        } else {
            ""
        }
    }

}