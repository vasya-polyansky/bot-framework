package io.github.vp.core

data class SelectorContext<TEvent : Any>(
    val pipeline: EventPipeline<TEvent>,
)