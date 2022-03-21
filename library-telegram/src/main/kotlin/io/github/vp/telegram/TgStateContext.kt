package io.github.vp.telegram

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.types.ChatId
import io.github.vp.core.plugin.fsm.StateContext

interface TgStateContext<T: TgStateContext<T>> : StateContext<T>, TgEventContext

@Suppress("FunctionName")
fun <T: TgStateContext<T>> TgStateContext(
    bot: TelegramBot,
    chatId: ChatId,
    stateContext: StateContext<T>,
): TgStateContext<T> {
    return object : TgStateContext<T>,
        TgEventContext by TgEventContext(bot, chatId),
        StateContext<T> by stateContext {}
}
