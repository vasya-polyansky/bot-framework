package io.github.vp.core

import io.github.vp.core.handlers.Handler


interface Registrar<TEvent : Any, TEventContext> {
    fun <R> register(handler: Handler<TEvent, TEventContext, R>)
}

/**
 * The order of parameters is strict
 */
fun <TEvent : Any, TEventContext, R> Registrar<TEvent, TEventContext>.register(
    trigger: Trigger<TEventContext, R>,
    selector: Selector<TEvent, R>,
) {
    register(Handler(selector, trigger))
}
