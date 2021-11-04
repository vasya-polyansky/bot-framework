package io.github.vp.framework.feature.fsm

import io.github.vp.framework.Registrar

class FsmRegistrar<TToken : Any, TEvent : Any, TEventContext>(
    private val config: FsmConfiguration<TEvent, TEventContext, TToken>,
) : Registrar<TEvent, TEventContext> by config.registrar {
    fun register(state: State<TEvent, TEventContext>, token: TToken) {
        state.register(token, config)
    }
}