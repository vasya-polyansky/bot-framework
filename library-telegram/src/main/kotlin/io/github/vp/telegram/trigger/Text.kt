package io.github.vp.telegram.trigger

import arrow.core.left
import arrow.core.right
import arrow.core.toOption
import io.github.vp.telegram.TelegramRegistrar
import dev.inmo.tgbotapi.extensions.utils.asBaseSentMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asCommonMessage
import dev.inmo.tgbotapi.extensions.utils.asSentMediaGroupUpdate
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.content.abstracts.MessageContent
import dev.inmo.tgbotapi.utils.PreviewFeature
import io.github.vp.core.Filter
import io.github.vp.core.Trigger
import io.github.vp.core.handlers.Handler


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
    registerHandler(
        Handler(
            trigger = trigger,
            selector = { update ->
                if (includeMediaGroups) {
                    // TODO: Refactor
                    update.asSentMediaGroupUpdate()
                        ?.data
                        ?.mapNotNull {
                            if (it.content is T) {
                                @Suppress("UNCHECKED_CAST")
                                val adaptedMessage = it as CommonMessage<T>
                                if (filter(adaptedMessage)) {
                                    adaptedMessage
                                } else {
                                    null
                                }
                            } else {
                                null
                            }
                        }?.let {
                            return@Handler it.right()
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
                    }?.let { return@Handler listOf(it).right() }

                return@Handler Unit.left()
            }
        )
    )
}