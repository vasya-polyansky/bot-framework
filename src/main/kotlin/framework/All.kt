package framework

import arrow.core.*
import dev.inmo.tgbotapi.extensions.utils.asBaseSentMessageUpdate
import dev.inmo.tgbotapi.extensions.utils.asCommonMessage
import dev.inmo.tgbotapi.extensions.utils.asSentMediaGroupUpdate
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.content.abstracts.MessageContent
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

//val app = TelegramApp(
//    bot = telegramBot("TOKEN???"),
//    stateStore = MyMemoryStateStore(GREETING)
//)
//
//val Greeting = app.state(
//    init = { send("Text me something") },
//    dispose = { send("Bye") }
//) {
//    onText("hello") {
//        send("Hi")
//        setState(MainMenu)
//    }
//}
//
//val MainMenu = app.state {
//    onText("") {
//        send("Not what?")
//    }

// TODO: Pass context and event to methods
//interface framework.Stage {
//    suspend fun init() // ???
//
//    suspend fun dispose() // ???
//
//    suspend fun handle() // ???
//}

fun <T> T?.toListOption(): Option<List<T>> = this?.let { Some(listOf(it)) } ?: None

typealias EventTrigger<C, E> = suspend C.(E) -> Unit


data class HandlerPair<E, R>(
    val filter: ResultingFilter<E, R>,
    val trigger: EventTrigger<TelegramEventContext, R>,
)

suspend fun <E, R> HandlerPair<E, R>.filterAndTrigger(
    event: E,
    context: TelegramEventContext,
) = filter(event).map { filterResults ->
    filterResults.forEach { trigger(context, it) }
}

class TelegramEventContext

// TODO: Add state binding
class TelegramDispatcher(
    private val updatesFlow: Flow<Update>,
    // TODO: Improve trigger context creation
    private val createEventContext: suspend (Update) -> TelegramEventContext,
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val rawHandlers = mutableSetOf<HandlerPair<Update, *>>()

    fun start() {
        updatesFlow
            // TODO: parallelize this flow iterations via markers
            .onEach { update ->
                val context = createEventContext(update)
                for (handler in rawHandlers) {
                    val result = handler.filterAndTrigger(update, context)
                    if (result.isNotEmpty()) {
                        break
                    }
                }
            }
            // TODO: Check if flow is already started
            .launchIn(scope)
    }

    fun stop() {
        scope.cancel()
    }

    fun <R> register(
        trigger: EventTrigger<TelegramEventContext, R>,
        filter: ResultingFilter<Update, R>,
    ) {
        rawHandlers.add(HandlerPair(filter, trigger))
    }
}


typealias TelegramEventTrigger<E> = EventTrigger<TelegramEventContext, E>
typealias BooleanFilter <E> = suspend (E) -> Boolean
// Iterable is used here because we can get multiple filtering results from one incoming event
typealias ResultingFilter<E, R> = suspend (E) -> Option<Iterable<R>>

fun TelegramDispatcher.onText(
    text: String,
    trigger: TelegramEventTrigger<ContentMessage<TextContent>>,
) {
    onText({ text == it.content.text }, trigger)
}

fun TelegramDispatcher.onText(
    filter: BooleanFilter<ContentMessage<TextContent>>,
    trigger: EventTrigger<TelegramEventContext, ContentMessage<TextContent>>,
) {
    onContent(includeMediaGroups = true, filter = filter, trigger = trigger)
}


@OptIn(PreviewFeature::class)
internal inline fun <reified T : MessageContent> TelegramDispatcher.onContent(
    includeMediaGroups: Boolean,
    noinline filter: BooleanFilter<CommonMessage<T>>,
    noinline trigger: TelegramEventTrigger<CommonMessage<T>>,
) {
    register(trigger) { update ->
        if (includeMediaGroups) {
            update.asSentMediaGroupUpdate()
                ?.data
                ?.mapNotNull {
                    if (it.content is T) {
                        @Suppress("UNCHECKED_CAST")
                        val adaptedMessage = it as CommonMessage<T>
                        if (filter(adaptedMessage)) adaptedMessage else null
                    } else {
                        null
                    }
                }.toOption()
        } else {
            update.asBaseSentMessageUpdate()
                ?.data
                ?.asCommonMessage()
                ?.let {
                    if (it.content is T) {
                        @Suppress("UNCHECKED_CAST")
                        val adaptedMessage = it as CommonMessage<T>
                        if (filter(adaptedMessage)) adaptedMessage else null
                    } else {
                        null
                    }
                }.toListOption()
        }
    }
}
