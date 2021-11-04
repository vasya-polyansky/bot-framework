package io.github.vp.telegram

import dev.inmo.tgbotapi.types.update.abstracts.Update
import io.github.vp.framework.feature.fsm.BaseFsm

typealias TelegramFsm = BaseFsm<Update, TelegramStateContext>
