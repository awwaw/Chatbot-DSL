package chatbot.dsl

import chatbot.api.*

class DefaultChatContextManager : ChatContextsManager {

    private val contexts: MutableMap<ChatId, ChatContext?> = HashMap()
    override fun getContext(chatId: ChatId): ChatContext? {
        return contexts[chatId]
    }

    override fun setContext(chatId: ChatId, newState: ChatContext?) {
        contexts[chatId] = newState
    }
}
