package io.github.vp.telegram

import dev.inmo.tgbotapi.types.update.abstracts.Update
import io.github.vp.core.Registrar

typealias TelegramRegistrar<TEventContext> = Registrar<Update, TEventContext>
