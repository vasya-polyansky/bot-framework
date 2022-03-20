package io.github.vp.core.feature.fsm

import arrow.core.None
import io.github.vp.core.EventPipeline
import io.github.vp.core.Registrar
import io.github.vp.core.handlers.Handler
import io.github.vp.core.stateStore.StateStore
import io.ktor.util.*

data class FsmConfiguration<TEvent : Any, TEventContext, TToken : Any>(
    val tokenKey: AttributeKey<TToken>,
    val stateTokenMap: StateTokenMap<TToken, TEventContext>,
    val stateStore: StateStore<TEventContext, TToken>,
    val registrar: Registrar<TEvent, TEventContext>,
    val pipeline: EventPipeline<TEvent>
) {
    fun registerState(
        state: State<TEvent, TEventContext>,
        token: TToken
    ) {
        stateTokenMap.saveStateAndToken(state, token)
        state.handlers
            .map { it.mapWithState(token, tokenKey) }
            .forEach { registrar.registerHandler(it) }
    }

    private fun <R> Handler<TEvent, TEventContext, R>.mapWithState(
        token: TToken,
        stateKey: AttributeKey<TToken>,
    ): Handler<TEvent, TEventContext, R> {
        return copy(
            selector = { if (pipeline.attributes[stateKey] != token) None else selector(it) }
        )
    }
}
