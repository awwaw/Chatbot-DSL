package chatbot.dsl

import chatbot.api.*

@DslMarker
annotation class BotDSL

@BotDSL
class Bot(
    private val client: Client,
) : ChatBot {
    override var logLevel: LogLevel = LogLevel.ERROR
    private var messageHandlers: List<Action<ChatContext?>> = mutableListOf()
    var contextManager: ChatContextsManager = DefaultChatContextManager()

    override fun processMessages(message: Message) {
        if (logLevel == LogLevel.INFO) {
            println("[INFO] precessing message $message")
        }

        val context = contextManager.getContext(message.chatId)
        val messageProcessorContext = MessageProcessorContext(message, client, context) { ctx ->
            contextManager.setContext(message.chatId, ctx)
        }

        for (action in messageHandlers) {
            if (action.predicate(this, message)) {
                action.action(messageProcessorContext)
                break
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

    fun behaviour(configure: IntoBuilder.() -> Unit) {
        val builder = IntoBuilder()
        builder.configure()
        messageHandlers = builder.actions
    }
}

fun chatBot(client: Client, configure: Bot.() -> Unit): ChatBot {
    val bot = Bot(client)
    bot.configure()
    return bot
}
