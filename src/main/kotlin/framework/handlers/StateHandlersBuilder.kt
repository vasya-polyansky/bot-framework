package framework.framework.handlers

import framework.Registrar
import framework.framework.stateStore.StateStore
import framework.feature.fsm.State
import framework.framework.feature.fsm.StateTokenMap

class StateHandlersBuilder<TEvent : Any, TEventContext> : Registrar<TEvent, TEventContext> {
    private val handlers = mutableSetOf<Handler<TEvent, TEventContext, *>>()

    override fun <R> register(handler: Handler<TEvent, TEventContext, R>) {
        handlers.add(handler)
    }

    fun build(): Iterable<Handler<TEvent, TEventContext, *>> = handlers
}
