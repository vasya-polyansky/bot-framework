package adapters.telegram

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.ChatId

interface TelegramSendingContext : TelegramEventContext, TelegramBot

fun TelegramSendingContext(bot: TelegramBot, chatId: ChatId): TelegramSendingContext =
    object : TelegramSendingContext, TelegramBot by bot {
        override val chatId = chatId
    }


suspend fun TelegramSendingContext.sendMessage(text: String) = sendMessage(chatId, text)
