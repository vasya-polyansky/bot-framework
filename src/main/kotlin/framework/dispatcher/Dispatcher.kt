package framework.dispatcher

import framework.feature.DispatcherFeature

interface Dispatcher<TEvent : Any> {
    fun <TConfiguration> install(
        feature: DispatcherFeature<TEvent, TConfiguration>,
        configure: TConfiguration.() -> Unit = {},
    )
}