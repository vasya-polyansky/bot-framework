package framework.feature

import framework.EventPipeline
import mu.KLogger
import mu.KotlinLogging

private val kLogger = KotlinLogging.logger { }

class Logging<TEvent : Any>(
    private val logger: KLogger = kLogger,
) : DispatcherFeature<TEvent, Unit> {
    override fun install(pipeline: EventPipeline<TEvent>, configure: Unit.() -> Unit) {
        pipeline.intercept(EventPipeline.Monitoring) {
            logger.info { "Incoming event: $context" }
        }
    }
}