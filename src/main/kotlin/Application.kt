package framework

import adapters.telegram.TelegramEventContext
import adapters.telegram.TelegramFsm
import adapters.telegram.TelegramStateContext
import adapters.telegram.sendMessage
import adapters.telegram.trigger.onText
import adapters.telegram.trigger.onUpdate
import dev.inmo.tgbotapi.bot.Ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.chat.get.getChat
import dev.inmo.tgbotapi.extensions.utils.asPrivateChat
import dev.inmo.tgbotapi.extensions.utils.extensions.sourceChat
import dev.inmo.tgbotapi.extensions.utils.updates.retrieving.longPollingFlow
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import framework.dispatcher.baseDispatcher
import framework.feature.EventHandling
import framework.feature.Logging
import framework.feature.fsm.State
import framework.framework.feature.fsm.FsmFeature
import framework.framework.feature.fsm.FsmRegistrar
import framework.framework.stateStore.MemoryStateStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

enum class MyStateValues { FIRST, SECOND }

val fsm = TelegramFsm()

// Required to annotate some states where the type checker can't do the type inference
typealias TelegramState = State<Update, TelegramStateContext>

val First: TelegramState = fsm.state(
    init = { sendMessage("Initializing first state") },
    dispose = { sendMessage("Disposing first state") },
) {
    onText("to second") {
        sendMessage("You're in first state")
        setState(Second)
    }
}

val Second = fsm.state(
    init = { sendMessage("Initializing second state") },
    dispose = { sendMessage("Disposing second state") },
) {
    onText("to first") {
        sendMessage("This message from second state")
        setState(First)
    }
}

typealias AppFsmRegistrar = FsmRegistrar<*, Update, TelegramStateContext>

@OptIn(PreviewFeature::class)
fun main() = runBlocking(Dispatchers.IO) {
    val bot = telegramBot(System.getenv("BOT_TOKEN"))

    val stateStore = MemoryStateStore<TelegramStateContext, MyStateValues>(
        MyStateValues.FIRST,
        compareContexts = { one, another -> one.chatId == another.chatId }
    )

    baseDispatcher(bot.longPollingFlow()) {
        install(Logging())

        install(
            FsmFeature(stateStore) { TelegramStateContext(bot, it.sourceChat()!!.id, this) }
        ) {
            basicTextHandlers()

            register(First, MyStateValues.FIRST)
            register(Second, MyStateValues.SECOND)
        }

        install(
            EventHandling.Fallback { TelegramEventContext(bot, it.sourceChat()!!.id) }
        ) {
            onText {
                sendMessage("Fallback text handling")
            }
        }
    }.start(this)
}

@OptIn(PreviewFeature::class)
fun AppFsmRegistrar.basicTextHandlers() {
    onText("hi", ignoreCase = true) {
        sendMessage("Oh, hello")
        setState(Second)
    }

    onText("name") {
        sendMessage("Your name: ${getChat(chatId).asPrivateChat()?.firstName}")
    }
}
