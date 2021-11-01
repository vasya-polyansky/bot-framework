package framework.feature

import framework.*
import framework.framework.SelectorContext
import framework.framework.handlers.HandlersBuilder
import framework.framework.handlers.selectAndTrigger


class EventHandling<TEvent : Any, TEventContext>(
    private val createEventContext: suspend (TEvent) -> TEventContext,
) : DispatcherFeature<TEvent, Registrar<TEvent, TEventContext>> {
    override fun install(
        pipeline: EventPipeline<TEvent>,
        configure: Registrar<TEvent, TEventContext>.() -> Unit,
    ) {
        val handlers = HandlersBuilder<TEvent, TEventContext>()
            .apply(configure)
            .build()

        val selectorContext = SelectorContext(pipeline)

        pipeline.intercept(EventPipeline.Event) {
            val event = context
            val eventContext = createEventContext(event)

            for (handler in handlers) {
                // TODO: parallelize this flow iterations via markers
                val result = handler.selectAndTrigger(event, selectorContext, eventContext)
                if (result.isNotEmpty()) {
                    finish()
                    break
                }
            }

        }
    }
}
