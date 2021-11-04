package io.github.vp.framework.stateStore

interface StateStore<TEventContext, TToken> {
    suspend fun getState(context: TEventContext): TToken

    suspend fun setState(context: TEventContext, token: TToken)
}