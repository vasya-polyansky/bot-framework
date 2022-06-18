package io.github.vp.telegram.trigger

import arrow.core.*
import io.github.vp.telegram.TgUpdateRegistrar
import dev.inmo.tgbotapi.extensions.utils.asBaseSentMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asCommonMessage
import dev.inmo.tgbotapi.extensions.utils.asSentMediaGroupUpdate
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.content.abstracts.MessageContent
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import io.github.vp.core.Filter
import io.github.vp.core.SimpleTrigger
import io.github.vp.core.handlers.Handler


fun <C> TgUpdateRegistrar<C>.onText(
    text: String,
    ignoreCase: Boolean = false,
    trigger: SimpleTrigger<C, CommonMessage<TextContent>>,
) {
    onText({ text.equals(it.content.text, ignoreCase = ignoreCase) }, trigger)
}

fun <C> TgUpdateRegistrar<C>.onText(
    trigger: SimpleTrigger<C, CommonMessage<TextContent>>,
) {
    onText(filter = { true }, trigger)
}


fun <C> TgUpdateRegistrar<C>.onText(
    filter: Filter<C, CommonMessage<TextContent>>,
    trigger: SimpleTrigger<C, CommonMessage<TextContent>>,
) {
    onContent(includeMediaGroups = true, filter = filter, trigger = trigger)
}

