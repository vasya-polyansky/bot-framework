package io.github.vp.core.stateStore

import io.github.vp.core.extension.indexOfFirstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * TContext object must implement structural equality
 */
open class MemoryStateStore<TContext, TToken>(
    private val initialState: TToken,
    private val compareContexts: (TContext, TContext) -> Boolean,
) : StateStore<TContext, TToken> {
    // TODO: Improve storing context
    private val list = mutableListOf<Pair<TContext, TToken>>()
    private val mutex = Mutex()

    override suspend fun getState(context: TContext): TToken =
        mutex.withLock {
            list.firstOrNull { compareContexts(it.first, context) }?.second ?: initialState
        }

    override suspend fun setState(context: TContext, token: TToken) {
        mutex.withLock {
            val index = list.indexOfFirstOrNull { compareContexts(it.first, context) }
            if (index != null) {
                list.removeAt(index)
            }

            list.add(context to token)
        }
    }
}
