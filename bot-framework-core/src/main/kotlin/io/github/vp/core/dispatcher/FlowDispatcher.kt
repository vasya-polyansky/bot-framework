package io.github.vp.core.dispatcher

import io.github.vp.core.EventPipeline
import io.github.vp.core.plugin.DispatcherPlugin
import io.ktor.util.pipeline.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.slf4j.LoggerFactory.getLogger

typealias OnException = suspend (Exception) -> Unit

class FlowDispatcher<TEvent : Any>(
    private val eventFlow: Flow<TEvent>,
    private val onException: OnException? = null,
) : Dispatcher<TEvent> {
    private val pipeline = EventPipeline<TEvent>()
    private val logger = getLogger(FlowDispatcher::class.java)

    override suspend fun startAndWait(scope: CoroutineScope) {
        logger.info("Starting dispatcher...")

        eventFlow
            .onEach {
                try {
                    pipeline.execute(it)
                } catch (e: Exception) {
                    if (onException != null) {
                        onException.invoke(e)
                    } else {
                        logger.error("Error while processing event", e)
                    }
                }
            }
            .onStart { logger.info("Dispatcher started 🚀") }
            .collect()
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
fun <TEvent : Any> FlowDispatcher(
    eventFlow: Flow<TEvent>,
    onException: OnException? = null,
    configure: Dispatcher<TEvent>.() -> Unit = {},
): Dispatcher<TEvent> {
    return FlowDispatcher(eventFlow, onException).apply { configure() }
}
