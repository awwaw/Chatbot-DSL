package chatbot.dsl

import chatbot.api.*

class Bot(
    private val client: Client,
) : ChatBot {
    override var logLevel: LogLevel = LogLevel.INFO
    private var messageHandlers: List<Action<ChatContext?>> = mutableListOf()
    private var contextManager: ChatContextsManager = DefaultChatContextManager()

    override fun processMessages(message: Message) {
        if (logLevel == LogLevel.INFO) {
            println("[INFO] precessing message $message")
        }

        val context = contextManager.getContext(message.chatId)
        val messageProcessorContext = MessageProcessorContext(message, client, context) {
            ctx -> contextManager.setContext(message.chatId, ctx)
        }

        for ((idx, action) in messageHandlers.withIndex()) {
            println(action.predicate)
            println(message.text)
            println(contextManager.getContext(message.chatId))
            println()
            val predicate = action.predicate(this, message)
            println("Predicate result is - $predicate")
            if (action.predicate(this, message)) {
                action.action(messageProcessorContext)
                println("Handler #$idx")
            }
        }
    }

    fun use(logLevel: LogLevel) {
        this.logLevel = logLevel
    }

    fun use(contextManager: ChatContextsManager) {
        this.contextManager = contextManager
    }

    operator fun LogLevel.unaryPlus() {
        logLevel = this
    }

    fun behaviour(configure: BehaviourBuilder<ChatContext?>.() -> Unit) {
        val builder = BehaviourBuilder<ChatContext?>()
        builder.configure()
        messageHandlers = builder.actions
    }


}

fun chatBot(client: Client, configure: Bot.() -> Unit): ChatBot {
    val bot = Bot(client)
    bot.configure()
    return bot
}
