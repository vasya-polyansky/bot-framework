package io.github.vp.telegram.trigger

import dev.inmo.tgbotapi.types.update.abstracts.Update
import io.github.vp.core.Trigger
import io.github.vp.core.register
import io.github.vp.core.toListOfOption
import io.github.vp.telegram.TelegramRegistrar

fun <C> TelegramRegistrar<C>.onUpdate(trigger: Trigger<C, Update>) {
    register(trigger) { it.toListOfOption() }
}
