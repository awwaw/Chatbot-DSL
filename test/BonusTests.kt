package chatbot.dsl

import chatbot.api.ChatContext
import chatbot.api.LogLevel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BonusTests : BaseTests() {

    val IS_ADMIN: MessagePredicate = { it.chatId.id == 316671439L }
    val IS_ODD_MESSAGE_ID: MessagePredicate = { it.id % 2 == 1L }

    @Test
    fun messagePredicate() {
        val bot = chatBot(testClient) {
            behaviour {
                IS_ADMIN.into {
                    onCommand("ban_user") {
                        sendMessage(message.chatId, "user banned")
                    }
                }

                onCommand("help") {
                    sendMessage(message.chatId, "usage: baa boo")
                }
            }
        }

        Assertions.assertEquals(LogLevel.ERROR, bot.logLevel)

        bot.processMessages(TEST_MESSAGE_HELP)
        assertMessage(11, "usage: baa boo")

        bot.processMessages(TEST_MESSAGE_BONUS)
        assertMessage(316671439L, "user banned")
    }

    object AdminAwaiting : ChatContext
    class AdminContext : ChatContext

    private fun baseTest(vararg predicates: MessagePredicate) {
        val operation: (MessagePredicate, MessagePredicate) -> MessagePredicate = { pred1, pred2 -> pred1 * pred2 }
        val PREDICATE = predicates.toList().fold(
            { true },
            operation,
        )
        val bot = chatBot(testClient) {
            behaviour {
                onCommand("login") {
                    client.sendMessage(message.chatId, "Waiting for admin to log in")
                    setContext(AdminAwaiting)
                }

                AdminAwaiting.into {
                    PREDICATE.into {
                        onMessage {
                            client.sendMessage(message.chatId, "Welcome, admin")
                            setContext(AdminContext())
                        }
                    }
                }

                into<AdminContext> {
                    onMessage {
                        client.sendMessage(message.chatId, "It's very cool to be an admin")
                    }
                }

                PREDICATE.into {
                    onMessage {
                        sendMessage(message.chatId, "Admin message")
                    }
                }
            }
        }

        Assertions.assertEquals(LogLevel.ERROR, bot.logLevel)

        bot.processMessages(TEST_MESSAGE_LOGIN)
        assertMessage(316671439L, "Waiting for admin to log in")

        bot.processMessages(TEST_MESSAGE_BONUS)
        assertMessage(316671439L, "Welcome, admin")

        bot.processMessages(TEST_MESSAGE_BONUS)
        assertMessage(316671439L, "It's very cool to be an admin")
    }

    @Test
    fun messagePredicateWithContext() {
        baseTest(IS_ADMIN)
    }

    @Test
    fun predicatesMultiplication() {
        baseTest(IS_ADMIN, IS_ODD_MESSAGE_ID)
    }
}
