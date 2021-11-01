package framework.feature.fsm

import arrow.core.None
import framework.framework.feature.fsm.FsmConfiguration
import framework.framework.feature.fsm.StateRegistrar
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
        config: FsmConfiguration<TEvent, TEventContext, TToken>,
    ) {
        config.stateTokenMap.saveToken(this, token)
        StateHandlersBuilder(this, config.stateStore, config.stateTokenMap)
            .apply(handlersBlock)
            .build()
            .map { it.mapWithState(token, config.stateKey) }
            .forEach { config.registrar.register(it) }
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

