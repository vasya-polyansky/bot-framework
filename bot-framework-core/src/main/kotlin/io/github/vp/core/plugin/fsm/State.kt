package io.github.vp.core.plugin.fsm

import io.github.vp.core.LifecycleHook
import io.github.vp.core.handlers.Handler

abstract class State<TEvent : Any, TEventContext>(
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
