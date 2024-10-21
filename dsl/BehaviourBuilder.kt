package chatbot.dsl

import chatbot.api.*

@BotDSL
open class BehaviourBuilder<T : ChatContext?> {
    var actions: MutableList<Action<T>> = mutableListOf()

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

    fun copyActions(): List<Action<T>> {
        val newList: MutableList<Action<T>> = mutableListOf()
        for (action in actions) {
            newList.add(action)
        }
        return newList
    }
}
