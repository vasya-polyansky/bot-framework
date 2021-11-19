package io.github.vp.core.handlers

import io.github.vp.core.Registrar

open class HandlersBuilder<TEvent : Any, TEventContext> : Registrar<TEvent, TEventContext> {
    private val handlers = mutableSetOf<Handler<TEvent, TEventContext, *>>()

    override fun <R> register(handler: Handler<TEvent, TEventContext, R>) {
        handlers.add(handler)
    }

    fun build(): Iterable<Handler<TEvent, TEventContext, *>> = handlers
}


