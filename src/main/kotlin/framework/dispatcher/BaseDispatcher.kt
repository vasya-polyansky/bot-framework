package framework.dispatcher

import framework.feature.DispatcherFeature
import framework.EventPipeline
import io.ktor.util.pipeline.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BaseDispatcher<TEvent : Any>(
    private val scope: CoroutineScope,
    private val eventFlow: Flow<TEvent>,
) : Dispatcher<TEvent> {
    private val pipeline = EventPipeline<TEvent>()

    fun start() =
        eventFlow
            .onEach { pipeline.execute(it) }
            .launchIn(scope)  // TODO: Check if flow is already started

    fun stop() {
        scope.cancel()
    }

    override fun <TConfiguration> install(
        feature: DispatcherFeature<TEvent, TConfiguration>,
        configure: TConfiguration.() -> Unit,
    ) {
        // TODO: Check if a feature is already installed
        feature.install(pipeline, configure)
    }
}