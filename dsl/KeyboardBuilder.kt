package chatbot.dsl

import chatbot.api.Keyboard

@BotDSL
class RowBuilder {
    private var row: MutableList<Keyboard.Button> = mutableListOf()

    fun button(text: String) {
        row.add(Keyboard.Button(text))
    }

    operator fun String.unaryMinus() {
        button(this)
    }

    fun getRow(): MutableList<Keyboard.Button> {
        return row
    }
}

@BotDSL
class KeyboardBuilder {
    var oneTime = false
    var keyboard: MutableList<MutableList<Keyboard.Button>> = mutableListOf()

    fun row(configure: RowBuilder.() -> Unit) {
        val rowBuilder = RowBuilder()
        rowBuilder.configure()
        keyboard.add(rowBuilder.getRow())
    }

    fun isEmpty(): Boolean {
        return keyboard.all { row -> row.isEmpty() }
    }
}
