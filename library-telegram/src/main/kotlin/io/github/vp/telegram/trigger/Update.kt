package io.github.vp.telegram.trigger

import dev.inmo.tgbotapi.types.update.abstracts.Update
import io.github.vp.framework.Trigger
import io.github.vp.framework.register
import io.github.vp.framework.toListOption
import io.github.vp.telegram.TelegramRegistrar

fun <C> TelegramRegistrar<C>.onUpdate(trigger: Trigger<C, Update>) {
    register(trigger) { it.toListOption() }
}
