package framework.feature

import arrow.core.Option
import framework.EventPipeline
import framework.EventTrigger
import framework.Registrar
import framework.ResultingFilter

class EventHandling<TEvent : Any, TEventContext>(
    private val triggerOnlyFirst: Boolean = true,
    private val createEventContext: suspend (TEvent) -> TEventContext,
) : DispatcherFeature<TEvent, Registrar<TEvent, TEventContext>> {
    override fun install(
        pipeline: EventPipeline<TEvent>,
        configure: Registrar<TEvent, TEventContext>.() -> Unit,
    ) {
        val handlers = HandlersBuilder<TEvent, TEventContext>().apply(configure).build()

        pipeline.intercept(EventPipeline.Event) {
            val event = context
            val eventContext = createEventContext(event)
            for (handler in handlers) {
                // TODO: parallelize this flow iterations via markers
                val result = handler.filterAndTrigger(event, eventContext)
                if (triggerOnlyFirst && result.isNotEmpty()) {
                    break
                }
            }
        }
    }
}


private class HandlersBuilder<TEvent, TEventContext> : Registrar<TEvent, TEventContext> {
    private val handlers = mutableSetOf<HandlerPair<TEvent, TEventContext, *>>()

    override fun <R> register(
        trigger: EventTrigger<TEventContext, R>,
        filter: ResultingFilter<TEvent, R>,
    ) {
        handlers.add(HandlerPair(filter = filter, trigger = trigger))
    }

    fun build(): Iterable<HandlerPair<TEvent, TEventContext, *>> = handlers
}


private data class HandlerPair<TEvent, TEventContext, R>(
    val filter: ResultingFilter<TEvent, R>,
    val trigger: EventTrigger<TEventContext, R>,
)

private suspend fun <TEvent, TEventContext, R> HandlerPair<TEvent, TEventContext, R>.filterAndTrigger(
    event: TEvent,
    context: TEventContext,
): Option<Unit> =
    filter(event).map { filterResults ->
        filterResults.forEach { trigger(context, it) }
    }
