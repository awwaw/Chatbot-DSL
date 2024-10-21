package chatbot.dsl

import chatbot.api.ChatContext
import chatbot.api.Message

typealias MessagePredicate = Bot.(Message) -> Boolean

class Action<T : ChatContext?>(
    var predicate: MessagePredicate,
    val action: MessageProcessor<T>,
)
