package io.github.vp.core.feature

import io.github.vp.core.EventPipeline

// TODO: Remove duplication when registering a new feature
// TODO: Add feature key
interface DispatcherFeature<TEvent : Any, TConfiguration> {
    fun install(pipeline: EventPipeline<TEvent>, configure: TConfiguration.() -> Unit)
}