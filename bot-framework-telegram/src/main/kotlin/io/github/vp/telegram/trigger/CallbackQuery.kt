package io.github.vp.telegram.trigger

import arrow.core.Option
import dev.inmo.tgbotapi.extensions.utils.asCallbackQueryUpdate
import dev.inmo.tgbotapi.types.CallbackQuery.CallbackQuery
import dev.inmo.tgbotapi.utils.PreviewFeature
import io.github.vp.core.Filter
import io.github.vp.core.SimpleTrigger
import io.github.vp.telegram.TgUpdateRegistrar

@OptIn(PreviewFeature::class)
fun <C, E : CallbackQuery> TgUpdateRegistrar<C>.onCallbackQuery(
    filter: Filter<C, E>? = null,
    trigger: SimpleTrigger<C, E>,
) {
    @Suppress("UNCHECKED_CAST")
    onUpdate(trigger, filter) { Option.fromNullable(it.asCallbackQueryUpdate()?.data as? E) }
}
