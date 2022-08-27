package io.github.vp.core.plugin.fsm

import java.lang.IllegalStateException
import java.util.*
import kotlin.reflect.KClass

/**
 * Token must implement structural equality.
 * States are compared by reference equality.
 */
// TODO:
//  - Rewrite this binding
//  - Improve storing of state to token binding algorithmically
class StateToTokenBinding<TToken : Any, TEventContext> {
    private val stateToToken = IdentityHashMap<State<*, TEventContext>, TToken>()
    private val tokenToState = mutableMapOf<TToken, State<*, TEventContext>>()

    override fun toString() = stateToToken.toString()

    fun getToken(state: State<*, TEventContext>) =
        stateToToken[state] ?: throw IllegalArgumentException("State is not registered: $state")

    fun bindStateToToken(state: State<*, TEventContext>, token: TToken) {
        if (stateToToken[state] != null) {
            throw IllegalArgumentException("State is already registered: $state")
        }
        stateToToken[state] = token
        tokenToState[token] = state
    }

    fun getState(token: TToken): State<*, TEventContext> =
        tokenToState[token] ?: throw IllegalArgumentException("State is not registered for token: $token")

    fun <TEventContext, S : State<*, TEventContext>> getStateByClass(stateClass: KClass<S>): S =
        stateToToken.keys.firstOrNull { it::class == stateClass }
            ?.let {
                @Suppress("UNCHECKED_CAST")
                it as? S
            }
            ?: throw IllegalStateException("State instance is not found")
}
