package io.github.vp.framework.feature.fsm

import io.github.vp.framework.Registrar

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