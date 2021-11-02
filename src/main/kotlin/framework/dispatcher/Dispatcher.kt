package framework.dispatcher

import framework.feature.DispatcherFeature
import kotlinx.coroutines.CoroutineScope


interface Dispatcher<TEvent : Any> {
    fun <TConfiguration> install(
        feature: DispatcherFeature<TEvent, TConfiguration>,
        configure: TConfiguration.() -> Unit = {},
    )

    fun start(scope: CoroutineScope)
}