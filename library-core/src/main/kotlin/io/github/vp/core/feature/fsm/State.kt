package io.github.vp.core.feature.fsm

import io.github.vp.core.LifecycleHook
import io.github.vp.core.handlers.Handler

open class State<TEvent : Any, TEventContext>(
    val init: LifecycleHook<TEventContext>? = null,
    val dispose: LifecycleHook<TEventContext>? = null,
    val handlers: Iterable<Handler<TEvent, TEventContext, *>>,
) {
    constructor(
        block: StateBuilder<TEvent, TEventContext>.() -> Unit,
    ) : this(
        StateBuilder<TEvent, TEventContext>()
            .apply(block)
            .buildState()
    )

    constructor(state: State<TEvent, TEventContext>) : this(state.init, state.dispose, state.handlers)
}
