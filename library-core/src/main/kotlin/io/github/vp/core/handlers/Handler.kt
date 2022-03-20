package io.github.vp.core.handlers

import arrow.core.Either
import arrow.core.right
import io.github.vp.core.Selector
import io.github.vp.core.Trigger

data class Handler<TEvent : Any, TEventContext, TSelected>(
    val selector: Selector<TEvent, TSelected>,
    val trigger: Trigger<TEventContext, TSelected>,
)

@Suppress("FunctionName")
fun <TEvent : Any, TEventContext> HandlerWithoutFilter(
    trigger: Trigger<TEventContext, TEvent>,
): Handler<TEvent, TEventContext, TEvent> {
    return Handler(
        selector = { listOf(it).right() },
        trigger = trigger
    )
}

suspend fun <TEvent : Any, TEventContext, R> Handler<TEvent, TEventContext, R>.triggerIfSelected(
    event: TEvent,
    eventContext: TEventContext,
): Either<Unit, Unit> {
    return selector(event).map { selectedResults ->
        selectedResults.forEach { trigger(eventContext, it) }
    }
}
