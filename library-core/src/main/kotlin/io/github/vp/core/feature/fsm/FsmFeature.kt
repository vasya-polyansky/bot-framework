package io.github.vp.core.feature.fsm

import io.github.vp.core.EventPipeline
import io.github.vp.core.stateStore.StateStore
import io.github.vp.core.feature.DispatcherFeature
import io.github.vp.core.feature.EventHandling
import io.ktor.util.*


class FsmFeature<TEvent : Any, TToken : Any, TEventContext : StateContext<TEventContext>>(
    private val stateStore: StateStore<TEventContext, TToken>,
    private val createEventContext: suspend StateContext<TEventContext>.(TEvent) -> TEventContext,
) : DispatcherFeature<TEvent, FsmRegistrar<TToken, TEvent, TEventContext>> {

    private val tokenKey = AttributeKey<TToken>("FSMTokenKey")
    private val stateKey = AttributeKey<State<*, TEventContext>>("FSMStateKey")

    private val stateTokenMap = StateTokenMap<TToken, TEventContext>()

    // TODO: Remove dummy state context
    private val dummyStateContext = DummyStateContextImpl<TEventContext>()

    override fun install(
        pipeline: EventPipeline<TEvent>,
        configure: FsmRegistrar<TToken, TEvent, TEventContext>.() -> Unit,
    ) {
        pipeline.intercept(EventPipeline.Setup) {
            val event = context
            val eventContext = createEventContext(dummyStateContext, event)
            val token = stateStore.getState(eventContext)
            val state = stateTokenMap.getState(token)

            pipeline.attributes.put(tokenKey, token)
            pipeline.attributes.put(stateKey, state)
        }

        EventHandling<TEvent, TEventContext> {
            val state = pipeline.attributes[stateKey]
            DefaultStateContext(state, stateStore, stateTokenMap).createEventContext(it)
        }.install(pipeline) {
            FsmRegistrar(
                FsmConfiguration(
                    tokenKey = tokenKey,
                    stateTokenMap = stateTokenMap,
                    stateStore = stateStore,
                    registrar = this
                )
            ).configure()
        }
    }
}


private class DefaultStateContext<TEvent : Any, TEventContext, TToken : Any>(
    private val currentState: State<TEvent, TEventContext>,
    private val stateStore: StateStore<TEventContext, TToken>,
    private val stateTokenMap: StateTokenMap<TToken, TEventContext>,
) : StateContext<TEventContext> {
    override suspend fun TEventContext.setState(nextState: State<*, TEventContext>) {
        currentState.dispose?.invoke(this)
        nextState.init?.invoke(this)

        val token = stateTokenMap.getToken(nextState)
        stateStore.setState(this, token)
    }
}

// TODO: Remove this class
/**
 * Helping class, must hot implement setState method
 */
private class DummyStateContextImpl<TEventContext> : StateContext<TEventContext> {
    override suspend fun TEventContext.setState(nextState: State<*, TEventContext>) {
        throw NotImplementedError("Set state must never be called on this context!")
    }
}