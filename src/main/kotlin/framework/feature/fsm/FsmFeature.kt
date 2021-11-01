package framework.framework.feature.fsm

import framework.EventPipeline
import framework.framework.stateStore.StateStore
import framework.feature.DispatcherFeature
import framework.feature.EventHandling
import io.ktor.util.*


class FsmFeature<TEvent : Any, TToken : Any, TEventContext>(
    private val stateStore: StateStore<TEventContext, TToken>,
    private val createEventContext: suspend (TEvent) -> TEventContext,
) : DispatcherFeature<TEvent, FsmRegistrar<TToken, TEvent, TEventContext>> {
    private val stateKey = AttributeKey<TToken>("FsmKey")
    private val stateTokenMap = StateTokenMap<TToken>()

    private val eventHandling = EventHandling(createEventContext)

    override fun install(
        pipeline: EventPipeline<TEvent>,
        configure: FsmRegistrar<TToken, TEvent, TEventContext>.() -> Unit,
    ) {
        pipeline.intercept(EventPipeline.Setup) {
            val eventContext = createEventContext(context)
            val state = stateStore.getState(eventContext)
            pipeline.attributes.put(stateKey, state)
        }

        eventHandling.install(pipeline) {
            FsmRegistrar(
                FsmConfiguration(
                    stateKey = stateKey,
                    stateTokenMap = stateTokenMap,
                    stateStore = stateStore,
                    registrar = this
                )
            ).configure()
        }
    }
}
