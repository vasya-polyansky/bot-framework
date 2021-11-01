package framework.framework

import framework.EventPipeline

data class SelectorContext<TEvent : Any>(
    val pipeline: EventPipeline<TEvent>,
)