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


@OptIn(PreviewFeature::class)
inline fun <reified T : MessageContent, C> TgUpdateRegistrar<C>.onContent(
    includeMediaGroups: Boolean,
    noinline filter: Filter<C, CommonMessage<T>>,
    noinline trigger: SimpleTrigger<C, CommonMessage<T>>,
) {
    registerHandler(
        Handler(
            trigger = trigger,
            selector = { selectCommonMessage(includeMediaGroups, it, filter) }
        )
    )
}

@OptIn(PreviewFeature::class)
suspend inline fun <reified T : MessageContent, C> C.selectCommonMessage(
    includeMediaGroups: Boolean,
    update: Update,
    noinline filter: Filter<C, CommonMessage<T>>,
): Either<Unit, Iterable<CommonMessage<T>>> {
    if (includeMediaGroups) {
        val messageList = update
            .asSentMediaGroupUpdate()
            ?.data
            ?.mapNotNull { it.toCommonMessage<T>().orNull() }
            ?.filter { filter(it) }

        if (messageList != null) {
            return messageList.right()
        }
    }

    val message = update.asBaseSentMessageUpdate()
        ?.data
        ?.asCommonMessage()
        ?.toCommonMessage<T>()
        ?.filter { filter(it) }
        ?.orNull()

    if (message != null) {
        return listOf(message).right()
    }

    return Unit.left()
}

inline fun <reified T : MessageContent> ContentMessage<*>.toCommonMessage(): Option<CommonMessage<T>> {
    return if (this.content is T) {
        @Suppress("UNCHECKED_CAST")
        Option(this as CommonMessage<T>)
    } else {
        None
    }
}