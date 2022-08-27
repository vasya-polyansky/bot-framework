package io.github.vp.core.plugin

import io.github.vp.core.EventPipeline
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger


class Logging<TEvent : Any>(
    private val logger: Logger = getLogger(Logging::class.java),
    private val eventToString: (TEvent) -> String = { it.toString() },
) : DispatcherPlugin<TEvent, Unit> {
    override fun install(pipeline: EventPipeline<TEvent>, configure: Unit.() -> Unit) {
        pipeline.intercept(EventPipeline.Monitoring) {
            logger.info("Incoming event: ${eventToString(context)}")
        }
    }
}
