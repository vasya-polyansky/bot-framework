package io.github.vp.core

import io.github.vp.core.handlers.HandlerWithoutFilter
import io.github.vp.core.handlers.HandlersBuilder
import io.github.vp.core.handlers.selectAndTrigger

fun <TEvent : Any, TEventContext, TFetched> Registrar<TEvent, TEventContext>.prefetch(
    fetcher: Prefetch<TEventContext, TFetched>,
    registrationBlock: Registrar<TEvent, TEventContext>.(TFetched) -> Unit,
) {
    registerHandler(
        HandlerWithoutFilter {
            val fetchedData = fetcher()

            val handlers = HandlersBuilder<TEvent, TEventContext>()
                .apply { registrationBlock(fetchedData) }
                .build()

            for (handler in handlers) {
                val result = handler.selectAndTrigger(it, this)
                if (result.isNotEmpty()) {
                    break
                }
            }
        }
    )
}
