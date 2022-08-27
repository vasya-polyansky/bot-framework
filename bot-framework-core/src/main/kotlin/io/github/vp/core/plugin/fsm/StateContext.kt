package io.github.vp.core.plugin.fsm

import kotlin.reflect.KClass

interface StateContext<TEventContext> {
    suspend fun TEventContext.setState(nextState: KClass<out State<*, TEventContext>>)
}
