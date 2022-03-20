package io.github.vp.core.plugin.fsm

import arrow.core.None
import io.github.vp.core.EventPipeline
import io.github.vp.core.Registrar
import io.github.vp.core.handlers.Handler
import io.ktor.util.*

class FsmRegistrar<TToken : Any, TEvent : Any, TEventContext>(
    private val tokenKey: AttributeKey<TToken>,
    private val stateTokenMap: StateToTokenBinding<TToken, TEventContext>,
    private val parentRegistrar: Registrar<TEvent, TEventContext>,
    private val pipeline: EventPipeline<TEvent>,
) : Registrar<TEvent, TEventContext> by parentRegistrar {
    fun register(state: State<TEvent, TEventContext>, token: TToken) {
        stateTokenMap.bindStateToToken(state, token)
        state.handlers
            .map { it.addStateFilter(token, tokenKey) }
            .forEach { registerHandler(it) }
    }

    private fun <R> Handler<TEvent, TEventContext, R>.addStateFilter(
        token: TToken,
        stateKey: AttributeKey<TToken>,
    ): Handler<TEvent, TEventContext, R> {
        return copy(
            selector = { if (pipeline.attributes[stateKey] != token) None else selector(it) }
        )
    }
}