package chatbot.dsl

import chatbot.api.Keyboard
import chatbot.api.MessageId

class MessageBuilder(var text: String) {
    var replyTo: MessageId? = null
    var keyboard: Keyboard? = null
    var keyboardPresent: Boolean = false

    fun removeKeyboard() {
        keyboard = Keyboard.Remove
        keyboardPresent = false
    }

    fun withKeyboard(configure: KeyboardBuilder.() -> Unit) {
        val keyboardBuilder = KeyboardBuilder()
        keyboardBuilder.configure()
        keyboardPresent = keyboardBuilder.isEmpty()
        keyboard = Keyboard.Markup(
            keyboardBuilder.oneTime,
            keyboardBuilder.keyboard
        )
    }

    fun isEmpty(): Boolean {
        return keyboardPresent && text.isEmpty() && replyTo == null
    }
}
