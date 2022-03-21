package io.github.vp.core

import io.github.vp.core.handlers.*

fun <TEvent : Any, TEventContext, TFetched> Registrar<TEvent, TEventContext>.prefetch(
    fetcher: Prefetch<TEventContext, TEvent, TFetched>,
    registrationBlock: Registrar<TEvent, TEventContext>.(TFetched) -> Unit,
) {
    registerHandler(
        HandlerWithoutFilter {
            val fetchedData = fetcher(it)

            val handlers = HandlersBuilder<TEvent, TEventContext>()
                .apply { registrationBlock(fetchedData) }
                .build()

            for (handler in handlers) {
                val result = handler.triggerIfSelected(it, this)
                if (result.isFinish()) {
                    return@HandlerWithoutFilter PipelineAction.Finish
                }
            }

            return@HandlerWithoutFilter PipelineAction.Continue
        }
    )
}
