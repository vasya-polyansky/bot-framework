@file:OptIn(PreviewFeature::class)

import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.chat.get.getChat
import dev.inmo.tgbotapi.extensions.utils.asPrivateChat
import dev.inmo.tgbotapi.extensions.utils.extensions.sourceChat
import dev.inmo.tgbotapi.extensions.utils.updates.retrieving.longPollingFlow
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import io.github.vp.core.Registrar
import io.github.vp.core.dispatcher.FlowDispatcher
import io.github.vp.core.plugin.Routing
import io.github.vp.core.plugin.Logging
import io.github.vp.core.plugin.fsm.StateMachine
import io.github.vp.core.plugin.fsm.State
import io.github.vp.core.stateStore.MemoryStateStore
import io.github.vp.telegram.TgEventContext
import io.github.vp.telegram.TgStateContext
import io.github.vp.telegram.ITgStateContext
import io.github.vp.telegram.sendMessage
import io.github.vp.telegram.trigger.onText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

enum class MyStateValues { FIRST, SECOND }


// region
// own types for app
typealias AppState = State<Update, AppContext>
typealias AppRegistrar = Registrar<Update, AppContext>

class AppContext(tgContext: ITgStateContext<AppContext>) : ITgStateContext<AppContext> by tgContext
// endregion


val First = AppState {
    init { sendMessage("Initializing first state 1️⃣") }
    dispose { sendMessage("Disposing first state 1️⃣") }

    onText("to second") {
        sendMessage("You're in first state 1️⃣")
        setState(Second)
    }
}

val Second: AppState = AppState {
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

        val stateStore = MemoryStateStore<AppContext, MyStateValues>(
            MyStateValues.FIRST,
            compareContexts = { one, another -> one.chatId == another.chatId }
        )

        FlowDispatcher(bot.longPollingFlow(), configure = {
            install(Logging())

            install(
                StateMachine(stateStore) {
                    val tgContext = TgStateContext(bot, it.sourceChat()!!.id, this)
                    AppContext(tgContext)
                }
            ) {
                basicTextHandlers()

                bind(First, MyStateValues.FIRST)
                bind(Second, MyStateValues.SECOND)
            }

            install(
                Routing.Fallback { TgEventContext(bot, it.sourceChat()!!.id) }
            ) {
                onText {
                    sendMessage("Fallback text handling (unknown)")
                }
            }
        }).startAndWait(this)
    }
}

fun AppRegistrar.basicTextHandlers() {
    onText("hi", ignoreCase = true) {
        sendMessage("Oh, hello. I move you to the second state ")
        setState(Second)
    }

    onText("name") {
        sendMessage("Your name: ${getChat(chatId).asPrivateChat()?.firstName}")
    }
}
