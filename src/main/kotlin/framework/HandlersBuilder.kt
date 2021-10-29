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
    val selector: ConvertingFilter<TEvent, R>,
    val trigger: EventTrigger<TEventContext, R>,
)

suspend fun <TEvent : Any, TEventContext, R> Handler<TEvent, TEventContext, R>.filterAndTrigger(
    event: TEvent,
    filterContext: FilterContext<TEvent>,
    eventContext: TEventContext,
): Option<Unit> =
    selector(filterContext, event).map { filterResults ->
        filterResults.forEach { trigger(eventContext, it) }
    }
