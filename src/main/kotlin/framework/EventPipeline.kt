package framework

import io.ktor.util.pipeline.*

class EventPipeline<TEvent : Any> : Pipeline<Unit, TEvent>(
    Setup,
    Monitoring,
    Features,
    Event,
    Fallback
) {
    companion object {
        /**
         * Phase for preparing call and it's attributes for processing
         */
        val Setup = PipelinePhase("Setup")

        /**
         * Phase for tracing calls, useful for logging, metrics, error handling and so on
         */
        val Monitoring = PipelinePhase("Monitoring")

        /**
         * Phase for features. Most features should intercept this phase.
         */
        val Features = PipelinePhase("Features")

        /**
         * Phase for processing a call and sending a response
         */
        val Event = PipelinePhase("Event")

        /**
         * Phase for handling unprocessed calls
         */
        val Fallback = PipelinePhase("Fallback")
    }
}