package io.github.vp.telegram

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.ChatId

interface TelegramEventContext : TelegramBot {
    val chatId: ChatId
}


fun TelegramEventContext(bot: TelegramBot, chatId: ChatId): TelegramEventContext =
    TelegramEventContextImpl(chatId, bot)


private data class TelegramEventContextImpl(
    override val chatId: ChatId,
    val bot: TelegramBot,
) : TelegramEventContext, TelegramBot by bot


suspend fun TelegramEventContext.sendMessage(text: String) = sendMessage(chatId, text)
