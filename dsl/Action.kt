package chatbot.dsl

import chatbot.api.ChatContext
import chatbot.api.Message

typealias MessagePredicate = Bot.(Message) -> Boolean

class Action<T : ChatContext?>(
    val predicate: MessagePredicate,
    val action: MessageProcessor<T>
)
