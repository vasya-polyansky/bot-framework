@file:OptIn(PreviewFeature::class)

package io.github.vp.telegram.trigger

import arrow.core.*
import io.github.vp.telegram.TgUpdateRegistrar
import dev.inmo.tgbotapi.extensions.utils.asBaseSentMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asCommonMessage
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import dev.inmo.tgbotapi.utils.PreviewFeature
import io.github.vp.core.Filter
import io.github.vp.core.SimpleTrigger

inline fun <C, reified T : MessageContent> TgUpdateRegistrar<C>.onContent(
    noinline filter: Filter<C, CommonMessage<T>>? = null,
    noinline trigger: SimpleTrigger<C, CommonMessage<T>>,
) {
    onUpdate(trigger, filter) { update ->
        Option.fromNullable(update.asBaseSentMessageUpdate()?.data?.asCommonMessage())
            .flatMap {
                @Suppress("UNCHECKED_CAST")
                if (it.content is T) Some(it as CommonMessage<T>) else None
            }
    }
}
