package framework.feature

import framework.EventPipeline
import framework.logger

class Logging<TEvent : Any> : DispatcherFeature<TEvent, Unit> {
    override fun install(pipeline: EventPipeline<TEvent>, configure: Unit.() -> Unit) {
        pipeline.intercept(EventPipeline.Monitoring) {
            logger.info { "Incoming event: $context" }
        }
    }
}