package io.github.vp.core.feature.fsm

import arrow.core.None
import io.github.vp.core.Registrar
import io.github.vp.core.handlers.Handler
import io.github.vp.core.handlers.StateHandlersBuilder
import io.ktor.util.*

typealias LifecycleHook<TEventContext> = suspend TEventContext.() -> Unit

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

    fun <TToken : Any> register(
        token: TToken,
        config: FsmConfiguration<TEvent, TEventContext, TToken>,
    ) {
        config.stateTokenMap.saveStateAndToken(this, token)
        handlers
            .map { it.mapWithState(token, config.tokenKey) }
            .forEach { config.registrar.register(it) }
    }
}

private fun <TEvent : Any, TEventContext, TToken : Any, R> Handler<TEvent, TEventContext, R>.mapWithState(
    token: TToken,
    stateKey: AttributeKey<TToken>,
): Handler<TEvent, TEventContext, R> = copy(
    selector = {
        if (pipeline.attributes[stateKey] != token) {
            None
        } else {
            selector(it)
        }
    }
)

