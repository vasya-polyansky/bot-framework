package io.github.vp.core.plugin

import io.github.vp.core.EventPipeline
import mu.KLogger
import mu.KotlinLogging

private val kLogger = KotlinLogging.logger { }

class Logging<TEvent : Any>(
    private val logger: KLogger = kLogger,
    private val eventToString: (TEvent) -> String = { it.toString() },
) : DispatcherPlugin<TEvent, Unit> {
    override fun install(pipeline: EventPipeline<TEvent>, configure: Unit.() -> Unit) {
        pipeline.intercept(EventPipeline.Monitoring) {
            logger.info { "Incoming event: ${eventToString(context)}" }
        }
    }
}