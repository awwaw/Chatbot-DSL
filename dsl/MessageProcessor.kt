package chatbot.dsl

import chatbot.api.ChatContext
import chatbot.api.ChatId
import chatbot.api.Client
import chatbot.api.Message

@BotDSL
class MessageProcessorContext<C : ChatContext?>(
    var message: Message,
    val client: Client,
    val context: C,
    val setContext: (c: ChatContext?) -> Unit,
) {
    fun sendMessage(chatId: ChatId, configureBuilder: MessageBuilder.() -> Unit) {
        val sender = MessageBuilder(message)
        sender.configureBuilder()
        if (!sender.isEmpty()) {
            client.sendMessage(
                chatId,
                sender.text,
                sender.keyboard,
                sender.replyTo,
            )
        }
    }

    fun sendMessage(chatId: ChatId, text: String) {
        client.sendMessage(
            chatId,
            text,
            null,
            null,
        )
    }
}

typealias MessageProcessor<C> = MessageProcessorContext<C>.() -> Unit
