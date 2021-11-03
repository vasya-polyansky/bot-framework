package adapters.telegram

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.types.ChatId
import framework.feature.fsm.StateContext

interface TelegramStateContext : StateContext<TelegramStateContext>, TelegramEventContext

fun TelegramStateContext(
    bot: TelegramBot,
    chatId: ChatId,
    stateContext: StateContext<TelegramStateContext>,
): TelegramStateContext = TelegramStateContextImpl(chatId, bot, stateContext)


private class TelegramStateContextImpl(
    chatId: ChatId,
    bot: TelegramBot,
    stateContext: StateContext<TelegramStateContext>,
) : TelegramStateContext,
    TelegramEventContext by TelegramEventContext(bot, chatId),
    StateContext<TelegramStateContext> by stateContext
