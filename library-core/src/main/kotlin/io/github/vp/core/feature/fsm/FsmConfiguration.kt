package io.github.vp.core.feature.fsm

import io.github.vp.core.Registrar
import io.github.vp.core.stateStore.StateStore
import io.ktor.util.*

data class FsmConfiguration<TEvent : Any, TEventContext, TToken : Any>(
    val tokenKey: AttributeKey<TToken>,
    val stateTokenMap: StateTokenMap<TToken, TEventContext>,
    val stateStore: StateStore<TEventContext, TToken>,
    val registrar: Registrar<TEvent, TEventContext>,
)