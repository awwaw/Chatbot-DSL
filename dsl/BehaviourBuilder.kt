package chatbot.dsl

import chatbot.api.*

class BehaviourBuilder<T : ChatContext?> {
    var actions: MutableList<Action<T>> = mutableListOf()
    var contextManager: ChatContextsManager = DefaultChatContextManager()

    fun onCommand(command: String, commandProcessor: MessageProcessor<T>) {
        val predicate: MessagePredicate = { message -> message.text.startsWith("/$command") }
        actions.add(Action(predicate, commandProcessor))
    }

    fun onMessage(predicate: MessagePredicate, messageProcessor: MessageProcessor<T>) {
        actions.add(Action(predicate, messageProcessor))
    }

    fun onMessagePrefix(prefix: String, messageProcessor: MessageProcessor<T>) {
        val predicate: MessagePredicate = { message -> message.text.startsWith(prefix) }
        actions.add(Action(predicate, messageProcessor))
    }

    fun onMessageContains(pattern: String, messageProcessor: MessageProcessor<T>) {
        val predicate: MessagePredicate = { message -> message.text.contains(pattern) }
        actions.add(Action(predicate, messageProcessor))
    }

    fun onMessage(messageText: String, messageProcessor: MessageProcessor<T>) {
        val predicate: MessagePredicate = { message -> message.text == messageText }
        actions.add(Action(predicate, messageProcessor))
    }

    fun onMessage(messageProcessor: MessageProcessor<T>) {
        val predicate: MessagePredicate = { true }
        actions.add(Action(predicate, messageProcessor))
    }

    inline fun <reified C : ChatContext?> intoImpl(
        configure: BehaviourBuilder<C>.() -> Unit,
        crossinline checkChatContext: (ChatContext?) -> Boolean
    ) {
        val builder = BehaviourBuilder<C>()
        builder.configure()
        builder.contextManager = contextManager

        for (action in builder.actions) {
            val newPredicate: MessagePredicate =
                { message ->
                    action.predicate(
                        this,
                        message
                    ) && checkChatContext(contextManager.getContext(message.chatId))
                }
            val newAction = action.action // MessageProcessor<C>

            cast<C, Action<T>>(Action(newPredicate, newAction))?.also { actions.add(it) }
        }
    }

    inline fun <F : ChatContext?, reified S : Action<T>> cast(action: Action<F>): S? {
        if (action is S) {
            return action
        }
        return null
    }

    inline fun <reified C : ChatContext?> into(configure: BehaviourBuilder<C>.() -> Unit) {
        intoImpl<C>(configure) { context -> context is C }
    }

    inline infix fun <reified C : ChatContext?> C.into(configure: BehaviourBuilder<C>.() -> Unit) {
        println("infix into")
        intoImpl<C>(configure) { context -> context == this@into }
    }
}
