import dev.inmo.tgbotapi.bot.Ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.chat.get.getChat
import dev.inmo.tgbotapi.extensions.utils.asPrivateChat
import dev.inmo.tgbotapi.extensions.utils.extensions.sourceChat
import dev.inmo.tgbotapi.extensions.utils.updates.retrieving.longPollingFlow
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import io.github.vp.framework.dispatcher.baseDispatcher
import io.github.vp.framework.feature.EventHandling
import io.github.vp.framework.feature.Logging
import io.github.vp.framework.feature.fsm.FsmFeature
import io.github.vp.framework.feature.fsm.FsmRegistrar
import io.github.vp.framework.feature.fsm.State
import io.github.vp.framework.stateStore.MemoryStateStore
import io.github.vp.telegram.TelegramEventContext
import io.github.vp.telegram.TelegramFsm
import io.github.vp.telegram.TelegramStateContext
import io.github.vp.telegram.sendMessage
import io.github.vp.telegram.trigger.onText
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
