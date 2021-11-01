package framework.feature.fsm

import arrow.core.None
import framework.Registrar
import framework.framework.stateStore.StateStore
import framework.framework.handlers.Handler
import framework.framework.handlers.StateHandlersBuilder
import io.ktor.util.*
import java.util.*


class State<TEvent : Any, TEventContext>(
    private val registrationBlock: StateRegistrar<TEvent, TEventContext>.() -> Unit,
) {
    fun <TToken : Any> register(
        token: TToken,
        stateKey: AttributeKey<TToken>,
        registrar: Registrar<TEvent, TEventContext>,
        stateTokenMap: StateTokenMap<TEvent, TEventContext, TToken>,
        stateStore: StateStore<TEventContext, TToken>,
    ) {
        stateTokenMap.saveToken(this, token)
        StateHandlersBuilder(stateStore, stateTokenMap)
            .apply(registrationBlock)
            .build()
            .map { it.mapWithState(token, stateKey) }
            .forEach { registrar.register(it) }
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

interface StateRegistrar<TEvent : Any, TEventContext> : Registrar<TEvent, TEventContext> {
    suspend fun TEventContext.setState(state: State<TEvent, TEventContext>)
}

// TODO: Maybe add interface for this class
/**
 * This class is required to automatically bind types for state
 */
open class BaseFsm<TEvent : Any, TEventContext> {
    fun state(
        block: StateRegistrar<TEvent, TEventContext>.() -> Unit,
    ): State<TEvent, TEventContext> = State(block)
}


class StateTokenMap<TEvent : Any, TEventContext, TToken> {
    private val map = IdentityHashMap<State<TEvent, TEventContext>, TToken>()

    override fun toString() = map.toString()

    private fun getTokenOrNull(state: State<TEvent, TEventContext>) = map[state]

    fun getToken(state: State<TEvent, TEventContext>) =
        getTokenOrNull(state) ?: throw IllegalArgumentException("State is not registered: $state")

    fun saveToken(state: State<TEvent, TEventContext>, token: TToken) {
        if (getTokenOrNull(state) != null) {
            throw IllegalArgumentException("State is already registered: $state")
        }
        map[state] = token
    }
}
