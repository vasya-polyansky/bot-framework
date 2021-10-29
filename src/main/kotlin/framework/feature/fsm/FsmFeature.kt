package framework.framework.feature.fsm

import framework.EventPipeline
import framework.Registrar
import framework.StateStore
import framework.feature.DispatcherFeature
import framework.feature.EventHandling
import framework.feature.fsm.State
import io.ktor.util.*

class FsmFeature<TEvent : Any, TToken : Any, TEventContext>(
    private val stateStore: StateStore<TEvent, TToken>,
    createEventContext: suspend (TEvent) -> TEventContext,
) : DispatcherFeature<TEvent, FsmContext<TToken, TEvent, TEventContext>> {
    private val stateKey = AttributeKey<TToken>("FsmKey")
    private val eventHandling = EventHandling(createEventContext = createEventContext)

    override fun install(
        pipeline: EventPipeline<TEvent>,
        configure: FsmContext<TToken, TEvent, TEventContext>.() -> Unit,
    ) {
        pipeline.intercept(EventPipeline.Setup) {
            val state = stateStore.getState(context)
            pipeline.attributes.put(stateKey, state)
        }

        eventHandling.install(pipeline) {
            FsmContext(stateKey, this).configure()
        }
    }
}


class FsmContext<TToken : Any, TEvent : Any, TEventContext> internal constructor(
    private val stateKey: AttributeKey<TToken>,
    private val registrar: Registrar<TEvent, TEventContext>,
) {
    fun register(
        state: State<TEvent, TEventContext>,
        token: TToken,
    ) {
        state.register(token, stateKey, registrar)
    }
}
