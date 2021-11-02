package framework.framework.feature.fsm

import framework.Registrar
import framework.feature.fsm.LifecycleHook
import framework.feature.fsm.State
import framework.feature.fsm.StateContext

// TODO: Maybe add interface for this class
open class BaseFsm<TEvent : Any, TEventContext : StateContext<TEventContext>> {
    fun state(
        init: LifecycleHook<TEventContext>? = null,
        dispose: LifecycleHook<TEventContext>? = null,
        handlersBlock: Registrar<TEvent, TEventContext>.() -> Unit,
    ): State<TEvent, TEventContext> =
        State(
            init = init,
            dispose = dispose,
            handlersBlock = handlersBlock
        )
}