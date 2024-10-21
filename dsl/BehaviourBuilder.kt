package chatbot.dsl

import chatbot.api.*

typealias MessagePredicate = (message: Message) -> Boolean

operator fun MessagePredicate.times(other: MessagePredicate): MessagePredicate {
    return { message ->
        this(message) && other(message)
    }
}

@BotDSL
open class BehaviourBuilder<T : ChatContext?> {
    var actions: MutableList<Action<T>> = mutableListOf()

    fun onCommand(command: String, commandProcessor: MessageProcessor<T>) {
        val predicate: Bot.(Message) -> Boolean = { message -> message.text.startsWith("/$command") }
        actions.add(Action(predicate, commandProcessor))
    }

    fun onMessage(predicate: Bot.(Message) -> Boolean, messageProcessor: MessageProcessor<T>) {
        actions.add(Action(predicate, messageProcessor))
    }

    fun onMessagePrefix(prefix: String, messageProcessor: MessageProcessor<T>) {
        val predicate: Bot.(Message) -> Boolean = { message -> message.text.startsWith(prefix) }
        actions.add(Action(predicate, messageProcessor))
    }

    fun onMessageContains(pattern: String, messageProcessor: MessageProcessor<T>) {
        val predicate: Bot.(Message) -> Boolean = { message -> message.text.contains(pattern) }
        actions.add(Action(predicate, messageProcessor))
    }

    fun onMessage(messageText: String, messageProcessor: MessageProcessor<T>) {
        val predicate: Bot.(Message) -> Boolean = { message -> message.text == messageText }
        actions.add(Action(predicate, messageProcessor))
    }

    fun onMessage(messageProcessor: MessageProcessor<T>) {
        val predicate: Bot.(Message) -> Boolean = { true }
        actions.add(Action(predicate, messageProcessor))
    }

    fun copyActions(): List<Action<T>> {
        val newList: MutableList<Action<T>> = mutableListOf()
        for (action in actions) {
            newList.add(action)
        }
        return newList
    }

    fun MessagePredicate.into(configure: BehaviourBuilder<T>.() -> Unit) {
        println("predicate into")
        val builder = BehaviourBuilder<T>()
        builder.configure()

        val tmpActions = builder.copyActions()
        for (action in tmpActions) {
            val newPredicate: Bot.(Message) -> Boolean = { message ->
                this@into(message) && action.predicate(this, message)
            }

            actions.add(Action(newPredicate, action.action))
        }
    }
}
