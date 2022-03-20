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

    private val stateToTokenBinding = StateToTokenBinding<TToken, TEventContext>()

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
            val state = stateToTokenBinding.getState(token)

            pipeline.attributes.put(tokenKey, token)
            pipeline.attributes.put(stateKey, state)
        }

        val eventHandlingFeature = EventHandling<TEvent, TEventContext>(
            createEventContext = {
                val state = pipeline.attributes[stateKey]
                val stateContext = FsmStateContext(state, stateStore, stateToTokenBinding)
                stateContext.createEventContext(it)
            }
        )

        eventHandlingFeature.install(pipeline) {
            val fsmRegistrar = FsmRegistrar(
                tokenKey = tokenKey,
                stateTokenMap = stateToTokenBinding,
                parentRegistrar = this,
                pipeline = pipeline
            )
            fsmRegistrar.configure()
        }
    }

    private class FsmStateContext<TEvent : Any, TEventContext, TToken : Any>(
        private val currentState: State<TEvent, TEventContext>,
        private val stateStore: StateStore<TEventContext, TToken>,
        private val stateTokenBinding: StateToTokenBinding<TToken, TEventContext>,
    ) : StateContext<TEventContext> {
        override suspend fun TEventContext.setState(nextState: State<*, TEventContext>) {
            currentState.dispose?.invoke(this)
            nextState.init?.invoke(this)

            val token = stateTokenBinding.getToken(nextState)
            stateStore.setState(this, token)
        }
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