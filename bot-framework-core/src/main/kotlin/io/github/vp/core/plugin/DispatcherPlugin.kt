package io.github.vp.core.plugin

import io.github.vp.core.EventPipeline

// TODO: Remove duplication when registering a new plugin
// TODO: Add plugin key
interface DispatcherPlugin<TEvent : Any, TConfiguration> {
    fun install(pipeline: EventPipeline<TEvent>, configure: TConfiguration.() -> Unit)
}