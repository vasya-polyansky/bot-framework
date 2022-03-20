package io.github.vp.core.dispatcher

import io.github.vp.core.plugin.DispatcherPlugin
import kotlinx.coroutines.CoroutineScope


interface Dispatcher<TEvent : Any> {
    fun <TConfiguration> install(
        plugin: DispatcherPlugin<TEvent, TConfiguration>,
        configure: TConfiguration.() -> Unit = {},
    )

    fun start(scope: CoroutineScope)
}