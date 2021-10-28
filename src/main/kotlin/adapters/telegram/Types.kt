package adapters.telegram

import dev.inmo.tgbotapi.types.update.abstracts.Update
import framework.Registrar

typealias TelegramRegistrar<TEventContext> = Registrar<Update, TEventContext>
