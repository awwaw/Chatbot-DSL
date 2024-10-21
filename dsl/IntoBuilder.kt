package chatbot.dsl

import chatbot.api.ChatContext

@BotDSL
class IntoBuilder : BehaviourBuilder<ChatContext?>() {
    inline fun <reified C : ChatContext> into(configure: BehaviourBuilder<C>.() -> Unit) {
        intoImpl<C>(configure) { context -> context is C }
    }

    inline infix fun <reified C : ChatContext> C.into(configure: BehaviourBuilder<C>.() -> Unit) {
        intoImpl<C>(configure) { context -> context == this@into }
    }

    inline fun <reified C : ChatContext> intoImpl(
        configure: BehaviourBuilder<C>.() -> Unit,
        crossinline checkContext: (ChatContext?) -> Boolean,
    ) {
        val builder = BehaviourBuilder<C>()
        builder.configure()
        val tmpActions = builder.copyActions()
        for (handler in tmpActions) {
            val newPredicate: MessagePredicate =
                { message ->
                    handler.predicate(
                        this,
                        message,
                    ) && checkContext(contextManager.getContext(message.chatId))
                }
            actions.add(
                Action(newPredicate) {
                    handler.action(
                        MessageProcessorContext(
                            this.message,
                            this.client,
                            this.context as C,
                            this.setContext,
                        ),
                    )
                },
            )
        }
    }
}
