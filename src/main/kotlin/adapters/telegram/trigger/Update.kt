package adapters.telegram.trigger

import adapters.telegram.TelegramRegistrar
import dev.inmo.tgbotapi.types.update.abstracts.Update
import framework.Trigger
import framework.register
import framework.toListOption

fun <C> TelegramRegistrar<C>.onUpdate(trigger: Trigger<C, Update>) {
    register(trigger) { it.toListOption() }
}
