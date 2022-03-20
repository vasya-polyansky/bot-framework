package io.github.vp.core

import arrow.core.Option

/**
 * Iterable is used here because we can get multiple selector results from one incoming event
*/
typealias Selector<TEvent, TSelected> = suspend (TEvent) -> Option<Iterable<TSelected>>
typealias Filter <TEvent> = suspend (TEvent) -> Boolean

typealias Trigger<TEventContext, TEvent> = suspend TEventContext.(TEvent) -> Unit
typealias Prefetch<TEventContext, TEvent, TFetched> = suspend TEventContext.(TEvent) -> TFetched
typealias LifecycleHook<TEventContext> = suspend TEventContext.() -> Unit
