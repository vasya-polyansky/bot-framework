package adapters.telegram.trigger

import adapters.telegram.TelegramRegistrar
import arrow.core.toOption
import dev.inmo.tgbotapi.extensions.utils.asBaseSentMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asCommonMessage
import dev.inmo.tgbotapi.extensions.utils.asSentMediaGroupUpdate
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.content.abstracts.MessageContent
import dev.inmo.tgbotapi.utils.PreviewFeature
import framework.Filter
import framework.Trigger
import framework.register
import framework.toListOption


fun <C> TelegramRegistrar<C>.onText(
    text: String,
    ignoreCase: Boolean = false,
    trigger: Trigger<C, CommonMessage<TextContent>>,
) {
    onText({ text.equals(it.content.text, ignoreCase = ignoreCase) }, trigger)
}

fun <C> TelegramRegistrar<C>.onText(
    trigger: Trigger<C, CommonMessage<TextContent>>,
) {
    onText(filter = { true }, trigger)
}


fun <C> TelegramRegistrar<C>.onText(
    filter: Filter<CommonMessage<TextContent>>,
    trigger: Trigger<C, CommonMessage<TextContent>>,
) {
    onContent(includeMediaGroups = true, filter = filter, trigger = trigger)
}


@OptIn(PreviewFeature::class)
internal inline fun <reified T : MessageContent, C> TelegramRegistrar<C>.onContent(
    includeMediaGroups: Boolean,
    noinline filter: Filter<CommonMessage<T>>,
    noinline trigger: Trigger<C, CommonMessage<T>>,
) {
    register(trigger) { update ->
        if (includeMediaGroups) {
            update.asSentMediaGroupUpdate()
                ?.data
                ?.mapNotNull {
                    if (it.content is T) {
                        @Suppress("UNCHECKED_CAST")
                        val adaptedMessage = it as CommonMessage<T>
                        if (filter(adaptedMessage)) adaptedMessage else null
                    } else {
                        null
                    }
                }?.let {
                    return@register it.toOption()
                }
        }

        update.asBaseSentMessageUpdate()
            ?.data
            ?.asCommonMessage()
            ?.let {
                if (it.content is T) {
                    @Suppress("UNCHECKED_CAST")
                    val adaptedMessage = it as CommonMessage<T>
                    if (filter(adaptedMessage)) adaptedMessage else null
                } else {
                    null
                }
            }.toListOption()
    }
}