package framework

import arrow.core.Option

/**
 * Iterable is used here because we can get multiple selector results from one incoming event
 */
typealias Selector<TEvent, R> = suspend SelectorContext<TEvent>.(TEvent) -> Option<Iterable<R>>
typealias Filter <TEvent> = suspend (TEvent) -> Boolean

typealias Trigger<TEventContext, TEvent> = suspend TEventContext.(TEvent) -> Unit
