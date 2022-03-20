package io.github.vp.core.handlers

import arrow.core.Option
import io.github.vp.core.Selector
import io.github.vp.core.Trigger
import io.github.vp.core.toListInOption

data class Handler<TEvent : Any, TEventContext, TSelected>(
    val selector: Selector<TEvent, TSelected>,
    val trigger: Trigger<TEventContext, TSelected>,
)

@Suppress("FunctionName")
fun <TEvent : Any, TEventContext> HandlerWithoutFilter(
    trigger: Trigger<TEventContext, TEvent>,
): Handler<TEvent, TEventContext, TEvent> {
    return Handler(
        selector = { it.toListInOption() },
        trigger = trigger
    )
}

suspend fun <TEvent : Any, TEventContext, R> Handler<TEvent, TEventContext, R>.selectAndTrigger(
    event: TEvent,
    eventContext: TEventContext,
): Option<Unit> {
    return selector(event).map { selectedResults ->
        selectedResults.forEach { trigger(eventContext, it) }
    }
}
