package framework.framework.feature.fsm

import framework.Registrar
import framework.framework.stateStore.StateStore
import io.ktor.util.*

data class FsmConfiguration<TEvent : Any, TEventContext, TToken>(
    val stateKey: AttributeKey<TToken>,
    val stateTokenMap: StateTokenMap<TToken>,
    val stateStore: StateStore<TEventContext, TToken>,
    val registrar: Registrar<TEvent, TEventContext>,
)