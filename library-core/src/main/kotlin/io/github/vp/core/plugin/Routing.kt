package io.github.vp.core.plugin

import io.github.vp.core.EventPipeline
import io.github.vp.core.Registrar
import io.github.vp.core.handlers.HandlersBuilder
import io.github.vp.core.handlers.triggerIfSelected
import io.ktor.util.pipeline.*

class Routing<TEvent : Any, TEventContext>(
    private val createEventContext: suspend (TEvent) -> TEventContext,
) : DispatcherPlugin<TEvent, Registrar<TEvent, TEventContext>> {
    override fun install(
        pipeline: EventPipeline<TEvent>,
        configure: Registrar<TEvent, TEventContext>.() -> Unit,
    ) {
        installRouting(
            EventPipeline.Event,
            pipeline,
            createEventContext,
            configure
        )
    }

    class Fallback<TEvent : Any, TEventContext>(
        private val createEventContext: suspend (TEvent) -> TEventContext,
    ) : DispatcherPlugin<TEvent, Registrar<TEvent, TEventContext>> {
        override fun install(
            pipeline: EventPipeline<TEvent>,
            configure: Registrar<TEvent, TEventContext>.() -> Unit,
        ) {
            installRouting(
                EventPipeline.Fallback,
                pipeline,
                createEventContext,
                configure
            )
        }
    }
}

private fun <TEvent : Any, TEventContext> installRouting(
    pipelinePhase: PipelinePhase,
    pipeline: EventPipeline<TEvent>,
    createEventContext: suspend (TEvent) -> TEventContext,
    configure: Registrar<TEvent, TEventContext>.() -> Unit,
) {
    val handlers = HandlersBuilder<TEvent, TEventContext>()
        .apply(configure)
        .build()

    pipeline.intercept(pipelinePhase) {
        val event = context
        val eventContext = createEventContext(event)

        for (handler in handlers) {
            // TODO: parallelize this flow iterations via markers
            val result = handler.triggerIfSelected(event, eventContext)
            if (result.isNotEmpty()) {
                finish()
                break
            }
        }
    }
}
