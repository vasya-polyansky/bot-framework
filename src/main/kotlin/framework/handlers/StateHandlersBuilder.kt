package framework.framework.handlers

import framework.framework.stateStore.StateStore
import framework.feature.fsm.State
import framework.framework.feature.fsm.StateRegistrar
import framework.framework.feature.fsm.StateTokenMap

class StateHandlersBuilder<TEvent : Any, TEventContext, TToken>(
    private val currentState: State<TEvent, TEventContext>,
    private val stateStore: StateStore<TEventContext, TToken>,
    private val stateTokenMap: StateTokenMap<TToken>,
) : StateRegistrar<TEvent, TEventContext> {
    private val handlers = mutableSetOf<Handler<TEvent, TEventContext, *>>()

    override fun <R> register(handler: Handler<TEvent, TEventContext, R>) {
        handlers.add(handler)
    }

    fun build(): Iterable<Handler<TEvent, TEventContext, *>> = handlers

    override suspend fun TEventContext.setState(nextState: State<TEvent, TEventContext>) {
        val token = stateTokenMap.getToken(nextState)

        currentState.dispose?.invoke(this)
        nextState.init?.invoke(this)

        stateStore.setState(this, token)
    }
}
