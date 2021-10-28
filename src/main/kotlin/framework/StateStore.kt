package framework

// TODO: Implement framework.feature.fsm.state setting
interface StateStore<TEventContext, TState> {
    suspend fun getState(context: TEventContext): TState
}