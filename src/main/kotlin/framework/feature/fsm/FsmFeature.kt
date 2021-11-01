package framework.framework.feature.fsm

import framework.EventPipeline
import framework.Registrar
import framework.framework.stateStore.StateStore
import framework.feature.DispatcherFeature
import framework.feature.EventHandling
import framework.feature.fsm.State
import framework.feature.fsm.StateTokenMap
import io.ktor.util.*

class FsmFeature<TEvent : Any, TToken : Any, TEventContext>(
    private val stateStore: StateStore<TEventContext, TToken>,
    private val createEventContext: suspend (TEvent) -> TEventContext,
) : DispatcherFeature<TEvent, FsmContext<TToken, TEvent, TEventContext>> {
    private val stateKey = AttributeKey<TToken>("FsmKey")
    private val stateTokenMap = StateTokenMap<TEvent, TEventContext, TToken>()

    private val eventHandling = EventHandling(createEventContext)

    override fun install(
        pipeline: EventPipeline<TEvent>,
        configure: FsmContext<TToken, TEvent, TEventContext>.() -> Unit,
    ) {
        pipeline.intercept(EventPipeline.Setup) {
            val eventContext = createEventContext(context)
            val state = stateStore.getState(eventContext)
            pipeline.attributes.put(stateKey, state)
        }

        eventHandling.install(pipeline) {
            FsmContext(
                stateKey = stateKey,
                registrar = this,
                stateTokenMap = stateTokenMap,
                stateStore = stateStore
            ).configure()
        }

        println("State token map: $stateTokenMap")
    }
}


class FsmContext<TToken : Any, TEvent : Any, TEventContext> internal constructor(
    private val stateKey: AttributeKey<TToken>,
    private val registrar: Registrar<TEvent, TEventContext>,
    private val stateTokenMap: StateTokenMap<TEvent, TEventContext, TToken>,
    private val stateStore: StateStore<TEventContext, TToken>,
) {
    fun register(state: State<TEvent, TEventContext>, token: TToken) {
        state.register(
            token = token,
            stateKey = stateKey,
            registrar = registrar,
            stateTokenMap = stateTokenMap,
            stateStore = stateStore
        )
    }
}
