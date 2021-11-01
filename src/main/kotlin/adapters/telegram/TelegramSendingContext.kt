package adapters.telegram

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.ChatId

interface TelegramSendingContext : TelegramEventContext, TelegramBot

fun TelegramSendingContext(
    bot: TelegramBot,
    chatId: ChatId,
): TelegramSendingContext =
    TelegramSendingContextImpl(chatId, bot)


private data class TelegramSendingContextImpl(
    override val chatId: ChatId,
    val bot: TelegramBot,
) : TelegramSendingContext, TelegramBot by bot


suspend fun TelegramSendingContext.sendMessage(text: String) = sendMessage(chatId, text)
