package io.github.vp.core.plugin.fsm

interface StateContext<TEventContext> {
    suspend fun TEventContext.setState(nextState: State<*, TEventContext>)
}
