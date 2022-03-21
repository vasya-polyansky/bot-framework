package io.github.vp.telegram.trigger

import dev.inmo.tgbotapi.types.update.abstracts.Update
import io.github.vp.core.SimpleTrigger
import io.github.vp.core.handlers.PipelineAction
import io.github.vp.core.handlers.HandlerWithoutFilter
import io.github.vp.telegram.TelegramRegistrar

fun <C> TelegramRegistrar<C>.onUpdate(trigger: SimpleTrigger<C, Update>) {
    registerHandler(
        HandlerWithoutFilter { PipelineAction.Finish }
    )
}
