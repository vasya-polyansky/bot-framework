package framework.framework.handlers

import framework.framework.stateStore.StateStore
import framework.feature.fsm.State
import framework.feature.fsm.StateRegistrar
import framework.feature.fsm.StateTokenMap

class StateHandlersBuilder<TEvent : Any, TEventContext, TToken>(
    private val stateStore: StateStore<TEventContext, TToken>,
    private val stateTokenMap: StateTokenMap<TEvent, TEventContext, TToken>,
) : StateRegistrar<TEvent, TEventContext> {
    private val handlers = mutableSetOf<Handler<TEvent, TEventContext, *>>()

    override fun <R> register(handler: Handler<TEvent, TEventContext, R>) {
        handlers.add(handler)
    }

    fun build(): Iterable<Handler<TEvent, TEventContext, *>> = handlers

    override suspend fun TEventContext.setState(state: State<TEvent, TEventContext>) {
        // TODO: Call lifecycle methods
        val token = stateTokenMap.getToken(state)
        stateStore.setState(this, token)
    }
}
