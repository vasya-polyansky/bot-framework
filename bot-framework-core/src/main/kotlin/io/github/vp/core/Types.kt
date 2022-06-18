package io.github.vp.core

import arrow.core.Option
import io.github.vp.core.handlers.PipelineAction

/**
 * Iterable is used here because we can get multiple selector results from one incoming event
*/
typealias Selector<TEventContext, TEvent, TSelected> = suspend TEventContext.(TEvent) -> Option<Iterable<TSelected>>
typealias Filter <TEventContext, TEvent> = suspend TEventContext.(TEvent) -> Boolean

private typealias VerboseTrigger<TEventContext, TEvent, TResult> = suspend TEventContext.(TEvent) -> TResult
typealias ResultingTrigger<TEventContext, TEvent> = VerboseTrigger<TEventContext, TEvent, PipelineAction>
typealias SimpleTrigger<TEventContext, TEvent> = VerboseTrigger<TEventContext, TEvent, Unit>

typealias Prefetch<TEventContext, TEvent, TFetched> = suspend TEventContext.(TEvent) -> TFetched
typealias LifecycleHook<TEventContext> = suspend TEventContext.() -> Unit
