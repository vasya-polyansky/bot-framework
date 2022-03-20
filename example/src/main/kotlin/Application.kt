@file:OptIn(PreviewFeature::class)

import dev.inmo.tgbotapi.bot.Ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.chat.get.getChat
import dev.inmo.tgbotapi.extensions.utils.asPrivateChat
import dev.inmo.tgbotapi.extensions.utils.extensions.sourceChat
import dev.inmo.tgbotapi.extensions.utils.updates.retrieving.longPollingFlow
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import io.github.vp.core.Registrar
import io.github.vp.core.dispatcher.BaseDispatcher
import io.github.vp.core.plugin.Routing
import io.github.vp.core.plugin.Logging
import io.github.vp.core.plugin.fsm.StateMachine
import io.github.vp.core.plugin.fsm.State
import io.github.vp.core.stateStore.MemoryStateStore
import io.github.vp.telegram.TelegramEventContext
import io.github.vp.telegram.TelegramStateContext
import io.github.vp.telegram.sendMessage
import io.github.vp.telegram.trigger.onText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

enum class MyStateValues { FIRST, SECOND }

typealias TgState = State<Update, TelegramStateContext>  // Required to create states
typealias TgRegistrar = Registrar<Update, TelegramStateContext>

val First = TgState {
    init { sendMessage("Initializing first state 1️⃣") }
    dispose { sendMessage("Disposing first state 1️⃣") }

    onText("to second") {
        sendMessage("You're in first state 1️⃣")
        setState(Second)
    }
}

val Second: TgState = TgState {
    init { sendMessage("Initializing second state 2️⃣") }
    dispose { sendMessage("Disposing second state 2️⃣") }

    onText("to first") {
        sendMessage("This message from second state 2️⃣")
        setState(First)
    }
}

fun main() {
    runBlocking(Dispatchers.IO) {
        val bot = telegramBot(System.getenv("BOT_TOKEN"))

        val stateStore = MemoryStateStore<TelegramStateContext, MyStateValues>(
            MyStateValues.FIRST,
            compareContexts = { one, another -> one.chatId == another.chatId }
        )

        BaseDispatcher(bot.longPollingFlow()) {
            install(Logging())

            install(
                StateMachine(stateStore) { TelegramStateContext(bot, it.sourceChat()!!.id, this) }
            ) {
                basicTextHandlers()

                register(First, MyStateValues.FIRST)
                register(Second, MyStateValues.SECOND)
            }

            install(
                Routing.Fallback { TelegramEventContext(bot, it.sourceChat()!!.id) }
            ) {
                onText {
                    sendMessage("Fallback text handling (unknown)")
                }
            }
        }.start(this)
    }
}

fun TgRegistrar.basicTextHandlers() {
    onText("hi", ignoreCase = true) {
        sendMessage("Oh, hello. I move you to the second state ")
        setState(Second)
    }

    onText("name") {
        sendMessage("Your name: ${getChat(chatId).asPrivateChat()?.firstName}")
    }
}
