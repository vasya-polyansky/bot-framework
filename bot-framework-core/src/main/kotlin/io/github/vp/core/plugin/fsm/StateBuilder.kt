package io.github.vp.core.plugin.fsm

import io.github.vp.core.LifecycleHook
import io.github.vp.core.handlers.HandlersBuilder
import io.github.vp.core.microutils.singleWritable

class StateBuilder<TEvent : Any, TEventContext> : HandlersBuilder<TEvent, TEventContext>() {
    private var initBlock: LifecycleHook<TEventContext>? by singleWritable(null)
    private var disposeBlock: LifecycleHook<TEventContext>? by singleWritable(null)

    fun buildState(): State<TEvent, TEventContext> =
        object : State<TEvent, TEventContext>(
            init = initBlock,
            dispose = disposeBlock,
            handlers = build()
        ) {}

    fun init(block: LifecycleHook<TEventContext>) {
        initBlock = block
    }

    fun dispose(block: LifecycleHook<TEventContext>) {
        disposeBlock = block
    }
}