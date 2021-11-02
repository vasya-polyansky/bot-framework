package framework.feature.fsm

interface StateContext<TEventContext> {
    suspend fun TEventContext.setState(nextState: State<*, TEventContext>)
}
