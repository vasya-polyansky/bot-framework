package io.github.vp.core.feature

import io.github.vp.core.SelectorContext
import io.github.vp.core.EventPipeline
import io.github.vp.core.Registrar
import io.github.vp.core.handlers.HandlersBuilder
import io.github.vp.core.handlers.selectAndTrigger
import io.ktor.util.pipeline.*

private fun <TEvent : Any, TEventContext> installEventFeature(
    pipelinePhase: PipelinePhase,
    pipeline: EventPipeline<TEvent>,
    createEventContext: suspend (TEvent) -> TEventContext,
    configure: Registrar<TEvent, TEventContext>.() -> Unit,
) {
    val handlers = HandlersBuilder<TEvent, TEventContext>()
        .apply(configure)
        .build()

    val selectorContext = SelectorContext(pipeline)

    pipeline.intercept(pipelinePhase) {
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


class EventHandling<TEvent : Any, TEventContext>(
    private val createEventContext: suspend (TEvent) -> TEventContext,
) : DispatcherFeature<TEvent, Registrar<TEvent, TEventContext>> {
    override fun install(
        pipeline: EventPipeline<TEvent>,
        configure: Registrar<TEvent, TEventContext>.() -> Unit,
    ) {
        installEventFeature(
            EventPipeline.Event,
            pipeline,
            createEventContext,
            configure
        )
    }

    class Fallback<TEvent : Any, TEventContext>(
        private val createEventContext: suspend (TEvent) -> TEventContext,
    ) : DispatcherFeature<TEvent, Registrar<TEvent, TEventContext>> {
        override fun install(
            pipeline: EventPipeline<TEvent>,
            configure: Registrar<TEvent, TEventContext>.() -> Unit,
        ) {
            installEventFeature(
                EventPipeline.Fallback,
                pipeline,
                createEventContext,
                configure
            )
        }
    }
}
