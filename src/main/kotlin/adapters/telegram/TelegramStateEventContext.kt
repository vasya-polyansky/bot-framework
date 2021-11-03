package adapters.telegram

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.types.ChatId
import framework.feature.fsm.StateContext

interface TelegramStateEventContext : StateContext<TelegramStateEventContext>, TelegramEventContext

fun TelegramStateEventContext(
    bot: TelegramBot,
    chatId: ChatId,
    stateContext: StateContext<TelegramStateEventContext>,
): TelegramStateEventContext = TelegramStateEventContextImpl(chatId, bot, stateContext)


private class TelegramStateEventContextImpl(
    chatId: ChatId,
    bot: TelegramBot,
    stateContext: StateContext<TelegramStateEventContext>,
) : TelegramStateEventContext,
    TelegramEventContext by TelegramEventContext(bot, chatId),
    StateContext<TelegramStateEventContext> by stateContext
