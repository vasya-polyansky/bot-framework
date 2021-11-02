package framework

import adapters.telegram.TelegramFsm
import adapters.telegram.TelegramEventContext
import adapters.telegram.TelegramStateEventContext
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
typealias TelegramState = State<Update, TelegramStateEventContext>

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

        install(
            // TODO: Make these type variables to be inferred
            FsmFeature<Update, MyStateValues, TelegramStateEventContext>(
                MemoryStateStore(
                    MyStateValues.FIRST,
                    areContextsEqual = { one, another -> one.chatId == another.chatId }
                )
            ) {
                TelegramStateEventContext(bot, it.sourceChat()!!.id, this)
            }
        ) {
            onText("hi", ignoreCase = true) {
                sendMessage("Oh, hello")
            }

            onText("name") {
                sendMessage("Your name: ${getChat(chatId).asPrivateChat()?.firstName}")
            }

            register(First, MyStateValues.FIRST)
            register(Second, MyStateValues.SECOND)
        }
    }

    dispatcher.start()
    logger.info { "Application started 🚀" }
}
