package framework

import arrow.core.Option

class HandlersBuilder<TEvent : Any, TEventContext> : Registrar<TEvent, TEventContext> {
    private val handlers = mutableSetOf<Handler<TEvent, TEventContext, *>>()

    override fun <R> register(handler: Handler<TEvent, TEventContext, R>) {
        handlers.add(handler)
    }

    fun build(): Iterable<Handler<TEvent, TEventContext, *>> = handlers
}


data class Handler<TEvent : Any, TEventContext, R>(
    val selector: Selector<TEvent, R>,
    val trigger: Trigger<TEventContext, R>,
)

suspend fun <TEvent : Any, TEventContext, R> Handler<TEvent, TEventContext, R>.selectAndTrigger(
    event: TEvent,
    selectorContext: SelectorContext<TEvent>,
    eventContext: TEventContext,
): Option<Unit> =
    selector(selectorContext, event).map { selectedResults ->
        selectedResults.forEach { trigger(eventContext, it) }
    }
