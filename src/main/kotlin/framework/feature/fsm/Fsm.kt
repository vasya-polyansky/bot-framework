package framework.framework.feature.fsm

import framework.EventPipeline
import framework.StateStore
import framework.feature.DispatcherFeature
import io.ktor.util.*

class Fsm<TEvent : Any, TState : Any>(
    private val stateKey: AttributeKey<TState>,
    private val stateStore: StateStore<TEvent, TState>,
) : DispatcherFeature<TEvent, Unit> {
    override fun install(
        pipeline: EventPipeline<TEvent>,
        configure: Unit.() -> Unit,
    ) {
        pipeline.intercept(EventPipeline.Setup) {
            val state = stateStore.getState(context)
            pipeline.attributes.put(stateKey, state)
        }
    }
}