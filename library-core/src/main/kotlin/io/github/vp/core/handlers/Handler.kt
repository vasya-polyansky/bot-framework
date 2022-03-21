package io.github.vp.core.handlers

import arrow.core.right
import io.github.vp.core.ResultingTrigger
import io.github.vp.core.Selector
import io.github.vp.core.SimpleTrigger

data class Handler<TEvent : Any, TEventContext, TSelected>(
    val selector: Selector<TEvent, TSelected>,
    val trigger: ResultingTrigger<TEventContext, TSelected>,
) {
    companion object {
        operator fun <TEvent : Any, TEventContext, TSelected> invoke(
            selector: Selector<TEvent, TSelected>,
            trigger: SimpleTrigger<TEventContext, TSelected>,
        ): Handler<TEvent, TEventContext, TSelected> {
            return Handler(
                selector = selector,
                trigger = {
                    trigger(it)
                    PipelineAction.Finish
                }
            )
        }
    }
}

@Suppress("FunctionName")
fun <TEvent : Any, TEventContext> HandlerWithoutFilter(
    trigger: ResultingTrigger<TEventContext, TEvent>,
): Handler<TEvent, TEventContext, TEvent> {
    return Handler(
        selector = { listOf(it).right() },
        trigger = trigger
    )
}

suspend fun <TEvent : Any, TEventContext, R> Handler<TEvent, TEventContext, R>.triggerIfSelected(
    event: TEvent,
    eventContext: TEventContext,
): PipelineAction {
    return selector(event).fold(
        { PipelineAction.Continue },
        { selectedResults ->
            // TODO: Improve this code
            val actions = selectedResults.map { trigger(eventContext, it) }
            return actions.first()
        }
    )
}
