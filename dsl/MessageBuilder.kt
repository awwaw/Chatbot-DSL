package chatbot.dsl

import chatbot.api.Keyboard
import chatbot.api.Message
import chatbot.api.MessageId

@BotDSL
class MessageBuilder(var message: Message) {
    var text: String = ""
    var replyTo: MessageId? = null
    var keyboard: Keyboard? = null
    private var keyboardPresent: Boolean = false

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
            keyboardBuilder.keyboard,
        )
    }

    fun isEmpty(): Boolean {
        return text == "" && replyTo == null &&
            (keyboard == null || keyboard!!.isEmpty())
    }
}
