package io.github.vp.core.feature.fsm

import io.github.vp.core.Registrar

class FsmRegistrar<TToken : Any, TEvent : Any, TEventContext>(
    private val config: FsmConfiguration<TEvent, TEventContext, TToken>,
) : Registrar<TEvent, TEventContext> by config.registrar {
    fun register(state: State<TEvent, TEventContext>, token: TToken) {
        state.register(token, config)
    }
}