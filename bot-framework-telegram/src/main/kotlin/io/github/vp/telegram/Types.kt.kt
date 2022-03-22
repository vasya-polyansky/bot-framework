package io.github.vp.telegram

import dev.inmo.tgbotapi.types.update.abstracts.Update
import io.github.vp.core.Registrar
import io.github.vp.core.plugin.fsm.State

typealias TgUpdateRegistrar<TEventContext> = Registrar<Update, TEventContext>

interface TgStateContext : ITgStateContext<TgStateContext>
typealias TgRegistrar = Registrar<Update, TgStateContext>
typealias TgState = State<Update, TgStateContext>

