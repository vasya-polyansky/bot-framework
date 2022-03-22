package io.github.vp.core

import io.github.vp.core.handlers.Handler

interface Registrar<TEvent : Any, TEventContext> {
    fun <R> registerHandler(handler: Handler<TEvent, TEventContext, R>)
}
