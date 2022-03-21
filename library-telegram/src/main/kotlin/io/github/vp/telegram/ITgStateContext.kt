package io.github.vp.telegram

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.types.ChatId
import io.github.vp.core.plugin.fsm.StateContext

// TODO: Remove recursive generic
interface ITgStateContext<T: ITgStateContext<T>> : StateContext<T>, TgEventContext

@Suppress("FunctionName")
fun <T: ITgStateContext<T>> TgStateContext(
    bot: TelegramBot,
    chatId: ChatId,
    stateContext: StateContext<T>,
): ITgStateContext<T> {
    return object : ITgStateContext<T>,
        TgEventContext by TgEventContext(bot, chatId),
        StateContext<T> by stateContext {}
}
