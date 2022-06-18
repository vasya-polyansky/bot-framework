package io.github.vp.telegram.trigger

import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import io.github.vp.core.Filter
import io.github.vp.core.SimpleTrigger
import io.github.vp.telegram.TgUpdateRegistrar

fun <C> TgUpdateRegistrar<C>.onText(
    text: String,
    ignoreCase: Boolean = false,
    trigger: SimpleTrigger<C, CommonMessage<TextContent>>,
) {
    onText({ text.equals(it.content.text, ignoreCase = ignoreCase) }, trigger)
}

fun <C> TgUpdateRegistrar<C>.onText(
    filter: Filter<C, CommonMessage<TextContent>>? = null,
    trigger: SimpleTrigger<C, CommonMessage<TextContent>>,
) {
    onContent(filter, trigger)
}
