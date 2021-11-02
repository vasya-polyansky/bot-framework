package framework.dispatcher

import framework.EventPipeline
import framework.feature.DispatcherFeature
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
        feature: DispatcherFeature<TEvent, TConfiguration>,
        configure: TConfiguration.() -> Unit,
    ) {
        // TODO: Check if a feature is already installed
        feature.install(pipeline, configure)
    }
}

fun <TEvent : Any> baseDispatcher(
    eventFlow: Flow<TEvent>,
    block: Dispatcher<TEvent>.() -> Unit,
): Dispatcher<TEvent> = BaseDispatcher(eventFlow).apply { block() }
