package adapters.telegram

import dev.inmo.tgbotapi.types.ChatId

interface TelegramEventContext {
    val chatId: ChatId
}