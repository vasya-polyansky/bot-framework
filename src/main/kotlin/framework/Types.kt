package framework

import arrow.core.Option

/**
 * Iterable is used here because we can get multiple filtering results from one incoming event
 */
typealias ConvertingFilter<TEvent, R> = suspend FilterContext<TEvent>.(TEvent) -> Option<Iterable<R>>
typealias BooleanFilter <TEvent> = suspend (TEvent) -> Boolean

typealias EventTrigger<TEventContext, TEvent> = suspend TEventContext.(TEvent) -> Unit
