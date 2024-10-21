package chatbot.dsl

import chatbot.api.ChatContext
import chatbot.api.Message

class Action<T : ChatContext?>(
    var predicate: Bot.(Message) -> Boolean,
    val action: MessageProcessor<T>,
)
