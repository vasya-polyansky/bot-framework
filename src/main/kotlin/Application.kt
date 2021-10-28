package framework

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

val logger = KotlinLogging.logger { }

@OptIn(PreviewFeature::class)
fun main() = runBlocking(Dispatchers.IO) {
    val bot = telegramBot(System.getenv("BOT_TOKEN"))

    val dispatcher = BaseDispatcher(
        scope = this,
        eventFlow = bot.longPollingFlow(),
    )

    dispatcher.apply {
        install(Logging())

        install(
            EventHandling { TelegramSendingContext(bot, it.sourceChat()?.id!!) }
        ) {
            onText("hi", ignoreCase = true) {
                sendMessage("Oh, hello")
            }

            onText("name") {
                sendMessage("Your name: ${getChat(chatId).asPrivateChat()?.firstName}")
            }

            onText {
                sendMessage("Last handler")
            }
        }
    }

    dispatcher.start()
    logger.info { "Application started!" }
}
