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
) : Dispatcher<TEvent> {
    private val pipeline = EventPipeline<TEvent>()

    override fun start(scope: CoroutineScope) {
        logger.info { "Starting dispatcher..." }

        eventFlow
            .onEach { pipeline.execute(it) }
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

    companion object {
        operator fun <TEvent : Any> invoke(
            eventFlow: Flow<TEvent>,
            block: BaseDispatcher<TEvent>.() -> Unit,
        ) = BaseDispatcher(eventFlow).apply { block() }
    }
}
