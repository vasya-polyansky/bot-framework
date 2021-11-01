package framework.framework.feature.fsm

import framework.Registrar
import framework.feature.fsm.State

interface StateRegistrar<TEvent : Any, TEventContext> : Registrar<TEvent, TEventContext> {
    suspend fun TEventContext.setState(nextState: State<TEvent, TEventContext>)
}