package adapters.telegram

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.types.ChatId
import framework.feature.fsm.StateContext

interface TelegramStateEventContext : StateContext<TelegramStateEventContext>, TelegramEventContext

fun TelegramStateEventContext(
    bot: TelegramBot,
    chatId: ChatId,
    stateContext: StateContext<TelegramStateEventContext>,
): TelegramStateEventContext = TelegramStateEventContextImpl(bot, chatId, stateContext)


private class TelegramStateEventContextImpl(
    private val bot: TelegramBot,
    override val chatId: ChatId,
    private val stateContext: StateContext<TelegramStateEventContext>,
) : TelegramStateEventContext,
    StateContext<TelegramStateEventContext> by stateContext,
    TelegramBot by bot
