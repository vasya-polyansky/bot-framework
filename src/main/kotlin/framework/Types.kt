package framework

import arrow.core.Option

typealias BooleanFilter <E> = suspend (E) -> Boolean
// Iterable is used here because we can get multiple filtering results from one incoming event
typealias ResultingFilter<E, R> = suspend (E) -> Option<Iterable<R>>
typealias EventTrigger<TEventContext, TEvent> = suspend TEventContext.(TEvent) -> Unit
