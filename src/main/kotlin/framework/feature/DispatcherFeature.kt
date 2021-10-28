package framework.feature

import framework.EventPipeline

// TODO: Remove duplication when registering a new feature
// TODO: Add feature key
interface DispatcherFeature<TEvent : Any, TConfiguration> {
    fun install(pipeline: EventPipeline<TEvent>, configure: TConfiguration.() -> Unit)
}