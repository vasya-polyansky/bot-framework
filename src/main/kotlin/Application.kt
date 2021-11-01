package framework

import adapters.telegram.TelegramFsm
import adapters.telegram.TelegramSendingContext
import adapters.telegram.sendMessage
import adapters.telegram.trigger.onText
import dev.inmo.tgbotapi.bot.Ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.chat.get.getChat
import dev.inmo.tgbotapi.extensions.utils.asPrivateChat
import dev.inmo.tgbotapi.extensions.utils.extensions.sourceChat
import dev.inmo.tgbotapi.extensions.utils.updates.retrieving.longPollingFlow
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import framework.dispatcher.BaseDispatcher
import framework.feature.EventHandling
import framework.feature.Logging
import framework.feature.fsm.State
import framework.framework.feature.fsm.FsmFeature
import framework.framework.stateStore.MemoryStateStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

enum class MyStateValues { FIRST, SECOND }

val fsm = TelegramFsm()

// Required to annotate some states where the type checker can't do the type inference
typealias TelegramState = State<Update, TelegramSendingContext>

// TODO: Implement setState method
// TODO: Implement lifecycle methods
val First: TelegramState = fsm.state {
    onText("first") {
        sendMessage("First State")
        setState(Second)
    }
}


val Second = fsm.state {
    onText("second") {
        sendMessage("Second State")
        setState(First)
    }
}


@OptIn(PreviewFeature::class)
fun main() = runBlocking(Dispatchers.IO) {
    val bot = telegramBot(System.getenv("BOT_TOKEN"))

    val dispatcher = BaseDispatcher(this, bot.longPollingFlow())

    // TODO: Remove duplication of event context creation blocks
    // TODO: Add fallback handlers section

    // The order in which EventHandling and FsmFeature are registered is important
    // because they are using the same pipeline phase.
    dispatcher.apply {
        install(Logging())

        // Stateless handlers
        install(
            EventHandling { TelegramSendingContext(bot, it.sourceChat()?.id!!) }
        ) {
            onText("hi", ignoreCase = true) {
                sendMessage("Oh, hello")
            }

            onText("name") {
                sendMessage("Your name: ${getChat(chatId).asPrivateChat()?.firstName}")
            }
        }

        // FSM handlers
        install(
            FsmFeature(
                MemoryStateStore(MyStateValues.FIRST)
            ) { TelegramSendingContext(bot, it.sourceChat()?.id!!) }
        ) {
            register(First, MyStateValues.FIRST)
            register(Second, MyStateValues.SECOND)
        }
    }

    dispatcher.start()
    logger.info { "Application started 🚀" }
}
