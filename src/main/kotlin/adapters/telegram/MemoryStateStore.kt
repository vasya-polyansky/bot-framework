package framework.stateStore

import framework.StateStore

enum class StateValues {
    INIT
}

class MemoryStateStore<C> : StateStore<C, StateValues> {
    override suspend fun getState(context: C): StateValues {
        return StateValues.INIT
    }
}


