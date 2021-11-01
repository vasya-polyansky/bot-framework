package framework.framework.handlers

import arrow.core.Option
import framework.Selector
import framework.SelectorContext
import framework.Trigger

data class Handler<TEvent : Any, TEventContext, R>(
    val selector: Selector<TEvent, R>,
    val trigger: Trigger<TEventContext, R>,
)

suspend fun <TEvent : Any, TEventContext, R> Handler<TEvent, TEventContext, R>.selectAndTrigger(
    event: TEvent,
    selectorContext: SelectorContext<TEvent>,
    eventContext: TEventContext,
): Option<Unit> =
    selector(selectorContext, event).map { selectedResults ->
        selectedResults.forEach { trigger(eventContext, it) }
    }
