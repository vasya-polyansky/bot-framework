package framework.feature.fsm

import arrow.core.None
import framework.Registrar
import framework.framework.feature.fsm.StateRegistrar
import framework.framework.feature.fsm.StateTokenMap
import framework.framework.stateStore.StateStore
import framework.framework.handlers.Handler
import framework.framework.handlers.StateHandlersBuilder
import io.ktor.util.*

typealias LifecycleHook<TEventContext> = suspend TEventContext.() -> Unit

class State<TEvent : Any, TEventContext>(
    val init: LifecycleHook<TEventContext>? = null,
    val dispose: LifecycleHook<TEventContext>? = null,
    private val handlersBlock: StateRegistrar<TEvent, TEventContext>.() -> Unit,
) {
    fun <TToken : Any> register(
        token: TToken,
        stateKey: AttributeKey<TToken>,
        registrar: Registrar<TEvent, TEventContext>,
        stateTokenMap: StateTokenMap<TToken>,
        stateStore: StateStore<TEventContext, TToken>,
    ) {
        stateTokenMap.saveToken(this, token)
        StateHandlersBuilder(this, stateStore, stateTokenMap)
            .apply(handlersBlock)
            .build()
            .map { it.mapWithState(token, stateKey) }
            .forEach { registrar.register(it) }
    }
}

private fun <TEvent : Any, TEventContext, TToken : Any, R> Handler<TEvent, TEventContext, R>.mapWithState(
    token: TToken,
    stateKey: AttributeKey<TToken>,
): Handler<TEvent, TEventContext, R> = copy(
    selector = {
        if (pipeline.attributes[stateKey] != token) {
            None
        } else {
            selector(it)
        }
    }
)

