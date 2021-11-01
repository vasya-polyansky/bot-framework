package framework.framework.feature.fsm

import framework.Registrar
import framework.feature.fsm.State

class FsmRegistrar<TToken : Any, TEvent : Any, TEventContext>(
    private val config: FsmConfiguration<TEvent, TEventContext, TToken>,
) : Registrar<TEvent, TEventContext> by config.registrar {
    fun register(state: State<TEvent, TEventContext>, token: TToken) {
        state.register(token, config)
    }
}