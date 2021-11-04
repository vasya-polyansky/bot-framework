package io.github.vp.framework

data class SelectorContext<TEvent : Any>(
    val pipeline: EventPipeline<TEvent>,
)