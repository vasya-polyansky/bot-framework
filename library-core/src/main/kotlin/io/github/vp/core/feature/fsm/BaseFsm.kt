package io.github.vp.core.feature.fsm


open class BaseFsm<TEvent : Any, TEventContext : StateContext<TEventContext>> {
    fun state(
        block: StateBuilder<TEvent, TEventContext>.() -> Unit,
    ): State<TEvent, TEventContext> =
        StateBuilder<TEvent, TEventContext>()
            .apply(block)
            .buildState()
}
