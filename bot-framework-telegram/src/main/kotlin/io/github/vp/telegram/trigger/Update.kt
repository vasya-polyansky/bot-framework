package io.github.vp.telegram.trigger

import arrow.core.Option
import arrow.core.Some
import arrow.core.left
import arrow.core.right
import dev.inmo.tgbotapi.types.update.abstracts.Update
import io.github.vp.core.Filter
import io.github.vp.core.SimpleTrigger
import io.github.vp.core.handlers.Handler
import io.github.vp.telegram.TgUpdateRegistrar

fun <C, E> TgUpdateRegistrar<C>.onUpdate(
    trigger: SimpleTrigger<C, E>,
    filter: Filter<C, E>? = null,
    updateToData: (Update) -> Option<E>,
) {
    registerHandler(
        Handler(
            trigger = trigger,
            selector = { update ->
                updateToData(update)
                    .filter { filter?.invoke(this, it) ?: true }
                    .map { listOf(it) }
                    .fold({ Unit.left() }, { it.right() })
            }
        )
    )
}

fun <C> TgUpdateRegistrar<C>.onAnyUpdate(trigger: SimpleTrigger<C, Update>) {
    onUpdate(trigger) { Some(it) }
}
