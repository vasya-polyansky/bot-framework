package io.github.vp.core.feature.fsm

import io.github.vp.core.handlers.HandlersBuilder
import io.github.vp.core.microutils.singleWritable

class StateBuilder<TEvent : Any, TEventContext> : HandlersBuilder<TEvent, TEventContext>() {
    private var initBlock: LifecycleHook<TEventContext>? by singleWritable(null)
    private var disposeBlock: LifecycleHook<TEventContext>? by singleWritable(null)

    fun buildState() = State(
        init = initBlock,
        dispose = disposeBlock,
        handlers = build()
    )

    fun init(block: LifecycleHook<TEventContext>) {
        initBlock = block
    }

    fun dispose(block: LifecycleHook<TEventContext>) {
        disposeBlock = block
    }
}