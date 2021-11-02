package framework.framework.feature.fsm

import framework.feature.fsm.State
import java.util.*

/**
 * Token must implement structural equality.
 * States are compared by reference equality.
 */
class StateTokenMap<TToken : Any, TEventContext> {
    // TODO: Improve storing of state to token binding
    private val stateToToken = IdentityHashMap<State<*, TEventContext>, TToken>()
    private val tokenToState = mutableMapOf<TToken, State<*, TEventContext>>()

    override fun toString() = stateToToken.toString()

    fun getToken(state: State<*, TEventContext>) =
        stateToToken[state] ?: throw IllegalArgumentException("State is not registered: $state")

    fun saveStateAndToken(state: State<*, TEventContext>, token: TToken) {
        if (stateToToken[state] != null) {
            throw IllegalArgumentException("State is already registered: $state")
        }
        stateToToken[state] = token
        tokenToState[token] = state
    }

    fun getState(token: TToken): State<*, TEventContext> =
        tokenToState[token] ?: throw IllegalArgumentException("State is not registered for token: $token")
}