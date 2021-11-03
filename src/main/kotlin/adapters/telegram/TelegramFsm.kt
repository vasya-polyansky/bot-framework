package adapters.telegram

import dev.inmo.tgbotapi.types.update.abstracts.Update
import framework.framework.feature.fsm.BaseFsm

typealias TelegramFsm = BaseFsm<Update, TelegramStateContext>
