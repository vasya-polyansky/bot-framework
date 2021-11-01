package framework.framework.feature.fsm

import framework.feature.fsm.LifecycleHook
import framework.feature.fsm.State

// TODO: Maybe add interface for this class
/**
 * This class is required to automatically bind types for state
 */
open class BaseFsm<TEvent : Any, TEventContext> {
    fun state(
        init: LifecycleHook<TEventContext>? = null,
        dispose: LifecycleHook<TEventContext>? = null,
        handlersBlock: StateRegistrar<TEvent, TEventContext>.() -> Unit,
    ): State<TEvent, TEventContext> =
        State(
            init = init,
            dispose = dispose,
            handlersBlock = handlersBlock
        )
}