package framework.framework.feature.fsm

import framework.feature.fsm.State
import java.util.*

class StateTokenMap<TToken> {
    private val map = IdentityHashMap<State<*, *>, TToken>()

    override fun toString() = map.toString()

    private fun getTokenOrNull(state: State<*, *>) = map[state]

    fun getToken(state: State<*, *>) =
        getTokenOrNull(state) ?: throw IllegalArgumentException("State is not registered: $state")

    fun saveToken(state: State<*, *>, token: TToken) {
        if (getTokenOrNull(state) != null) {
            throw IllegalArgumentException("State is already registered: $state")
        }
        map[state] = token
    }
}