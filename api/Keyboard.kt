package chatbot.api

sealed interface Keyboard {
    data object Remove : Keyboard
    data class Markup(
        val oneTime: Boolean,
        val keyboard: List<List<Button>>,
    ) : Keyboard

    data class Button(val text: String)

    fun isEmpty(): Boolean {
        return when (this) {
            is Markup -> keyboard.all { row -> row.isEmpty() }
            Remove -> false
        }
    }
}
