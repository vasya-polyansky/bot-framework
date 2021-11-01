package framework.framework.stateStore

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * TEventContext object must implement structural equality
 */
open class MemoryStateStore<TEventContext, TToken>(
    private val initialState: TToken,
) : StateStore<TEventContext, TToken> {
    private val map = mutableMapOf<TEventContext, TToken>()
    private val mutex = Mutex()

    override suspend fun getState(context: TEventContext): TToken =
        mutex.withLock { map[context] ?: initialState }

    override suspend fun setState(context: TEventContext, token: TToken) {
        mutex.withLock { map[context] = token }
    }
}