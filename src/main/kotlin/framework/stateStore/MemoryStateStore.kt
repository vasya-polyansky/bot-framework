package framework.framework.stateStore

import framework.extension.indexOfFirstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * TEventContext object must implement structural equality
 */
open class MemoryStateStore<TEventContext, TToken>(
    private val initialState: TToken,
    private val areContextsEqual: (TEventContext, TEventContext) -> Boolean,
) : StateStore<TEventContext, TToken> {
    // TODO: Improve storing context
    private val list = mutableListOf<Pair<TEventContext, TToken>>()
    private val mutex = Mutex()

    override suspend fun getState(context: TEventContext): TToken =
        mutex.withLock {
            println("Memory state store: $list")
            list.firstOrNull { areContextsEqual(it.first, context) }?.second ?: initialState
        }

    override suspend fun setState(context: TEventContext, token: TToken) {
        mutex.withLock {
            val index = list.indexOfFirstOrNull { areContextsEqual(it.first, context) }
            if (index != null) {
                list.removeAt(index)
            }

            list.add(context to token)
        }
    }
}
