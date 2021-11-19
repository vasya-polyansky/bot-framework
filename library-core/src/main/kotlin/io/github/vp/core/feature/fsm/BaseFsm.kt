package io.github.vp.core.feature.fsm

import io.github.vp.core.handlers.HandlersBuilder


open class BaseFsm<TEvent : Any, TEventContext : StateContext<TEventContext>> {
    fun state(
        block: StateRegistrar<TEvent, TEventContext>.() -> Unit,
    ): State<TEvent, TEventContext> =
        StateRegistrar<TEvent, TEventContext>()
            .apply(block)
            .buildState()
}


class StateRegistrar<TEvent : Any, TEventContext> : HandlersBuilder<TEvent, TEventContext>() {
    private var initBlock: LifecycleHook<TEventContext>? = null
    private var disposeBlock: LifecycleHook<TEventContext>? = null

    fun buildState() = State(
        init = initBlock,
        dispose = disposeBlock,
        handlers = build()
    )

    fun init(block: LifecycleHook<TEventContext>? = null) {
        initBlock = block
    }

    fun dispose(block: LifecycleHook<TEventContext>? = null) {
        disposeBlock = block
    }
}