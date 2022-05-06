package io.github.vp.core.dispatcher

import io.github.vp.core.EventPipeline
import io.github.vp.core.plugin.DispatcherPlugin
import io.ktor.util.pipeline.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }


class BaseDispatcher<TEvent : Any>(
    private val eventFlow: Flow<TEvent>,
    private val onException: suspend (Exception) -> Unit =
        { logger.error("Error while processing event", it) }
) : Dispatcher<TEvent> {
    private val pipeline = EventPipeline<TEvent>()

    override fun start(scope: CoroutineScope) {
        logger.info { "Starting dispatcher..." }

        eventFlow
            .onEach {
                try {
                    pipeline.execute(it)
                } catch (e: Exception) {
                    onException(e)
                }
            }
            .onStart { logger.info { "Dispatcher started 🚀" } }
            .launchIn(scope)
    }

    override fun <TConfiguration> install(
        plugin: DispatcherPlugin<TEvent, TConfiguration>,
        configure: TConfiguration.() -> Unit,
    ) {
        // TODO: Check if a plugin is already installed
        plugin.install(pipeline, configure)
    }
}

@Suppress("FunctionName")
fun <TEvent : Any> BaseDispatcher(
    eventFlow: Flow<TEvent>,
    onException: suspend (Exception) -> Unit =
        { logger.error("Error while processing event", it) },
    configure: Dispatcher<TEvent>.() -> Unit = {},
): Dispatcher<TEvent> {
    return BaseDispatcher(eventFlow, onException).apply { configure() }
}
