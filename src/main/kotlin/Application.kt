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
import dev.inmo.tgbotapi.utils.PreviewFeature
import framework.dispatcher.BaseDispatcher
import framework.feature.EventHandling
import framework.feature.Logging
import framework.framework.feature.fsm.FsmFeature
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

val logger = KotlinLogging.logger { }

enum class MyStateValues { IDLE, MAIN_MENU }

class MemoryStateStore<C> : StateStore<C, MyStateValues> {
    override suspend fun getState(context: C): MyStateValues {
        return MyStateValues.MAIN_MENU
    }
}


val fsm = TelegramFsm()

// TODO: Implement setState method
// TODO: Implement lifecycle methods
val MainMenu = fsm.state {
    onText("menu") {
        sendMessage("The main menu answer")
    }
}


@OptIn(PreviewFeature::class)
fun main() = runBlocking(Dispatchers.IO) {
    val bot = telegramBot(System.getenv("BOT_TOKEN"))

    val dispatcher = BaseDispatcher(
        scope = this,
        eventFlow = bot.longPollingFlow(),
    )

    // TODO: Remove duplication of event context creation blocks
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
            FsmFeature(MemoryStateStore()) { TelegramSendingContext(bot, it.sourceChat()?.id!!) }
        ) {
            register(MainMenu, MyStateValues.MAIN_MENU)
        }
    }

    dispatcher.start()
    logger.info { "Application started 🚀" }
}
