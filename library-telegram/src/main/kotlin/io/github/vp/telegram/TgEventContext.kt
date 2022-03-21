package io.github.vp.telegram

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.ChatId

interface TgEventContext : TelegramBot {
    val chatId: ChatId
}


@Suppress("FunctionName")
fun TgEventContext(
    bot: TelegramBot,
    chatId: ChatId,
): TgEventContext {
    return object : TgEventContext, TelegramBot by bot {
        override val chatId = chatId
    }
}

suspend fun TgEventContext.sendMessage(text: String) = sendMessage(chatId, text)
