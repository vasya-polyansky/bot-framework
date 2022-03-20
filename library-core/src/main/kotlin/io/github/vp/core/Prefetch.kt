package io.github.vp.core

import io.github.vp.core.handlers.HandlerWithoutFilter
import io.github.vp.core.handlers.HandlersBuilder
import io.github.vp.core.handlers.triggerIfSelected

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
                if (result.isNotEmpty()) {
                    break
                }
            }

            // TODO: Return something to indicate that no one handler is selected. Required to check other handlers
            //  – May be replace Option with own sealed class
        }
    )
}
