package framework

import arrow.core.*
import dev.inmo.tgbotapi.bot.Ktor.telegramBot
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.chat.get.getChat
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.utils.*
import dev.inmo.tgbotapi.extensions.utils.extensions.sourceChat
import dev.inmo.tgbotapi.extensions.utils.updates.retrieving.longPollingFlow
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.content.abstracts.MessageContent
import dev.inmo.tgbotapi.types.update.abstracts.Update
import dev.inmo.tgbotapi.utils.PreviewFeature
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

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

typealias EventTrigger<TEventContext, TEvent> = suspend TEventContext.(TEvent) -> Unit


data class HandlerPair<TEvent, TEventContext, R>(
    val filter: ResultingFilter<TEvent, R>,
    val trigger: EventTrigger<TEventContext, R>,
)

suspend fun <TEvent, TEventContext, R> HandlerPair<TEvent, TEventContext, R>.filterAndTrigger(
    event: TEvent,
    context: TEventContext,
): Option<Unit> =
    filter(event).map { filterResults ->
        filterResults.forEach { trigger(context, it) }
    }


interface TelegramEventContext {
    val chatId: ChatId
}

interface TelegramSendingContext : TelegramEventContext, TelegramBot

fun TelegramSendingContext(bot: TelegramBot, chatId: ChatId): TelegramSendingContext =
    object : TelegramSendingContext, TelegramBot by bot {
        override val chatId = chatId
    }


suspend fun TelegramSendingContext.sendMessage(text: String) = sendMessage(chatId, text)


// TODO: Add state binding


interface Registrar<TEvent, TEventContext> {
    fun <R> register(
        trigger: EventTrigger<TEventContext, R>,
        filter: ResultingFilter<TEvent, R>,
    )
}

typealias TelegramRegistrar<TEventContext> = Registrar<Update, TEventContext>


class EventPipeline<TEvent : Any> : Pipeline<Unit, TEvent>(
    Setup,
    Monitoring,
    Features,
    Event,
    Fallback
) {
    companion object {
        /**
         * Phase for preparing call and it's attributes for processing
         */
        val Setup = PipelinePhase("Setup")

        /**
         * Phase for tracing calls, useful for logging, metrics, error handling and so on
         */
        val Monitoring = PipelinePhase("Monitoring")

        /**
         * Phase for features. Most features should intercept this phase.
         */
        val Features = PipelinePhase("Features")

        /**
         * Phase for processing a call and sending a response
         */
        val Event = PipelinePhase("Event")

        /**
         * Phase for handling unprocessed calls
         */
        val Fallback = PipelinePhase("Fallback")
    }
}


class Database {
    suspend fun getState(context: TelegramEventContext): String {
        ApplicationCallPipeline
        return "DEMO_STATE"
    }
}

// TODO: Implement state setting
interface StateStore<TEventContext, TState> {
    suspend fun getState(context: TEventContext): TState
}

interface Dispatcher<TEvent : Any> {
    fun <TConfiguration> install(
        feature: DispatcherFeature<TEvent, TConfiguration>,
        configure: TConfiguration.() -> Unit = {},
    )
}

// TODO: Remove duplication when registering a new feature
// TODO: Add feature key
interface DispatcherFeature<TEvent : Any, TConfiguration> {
    fun install(pipeline: EventPipeline<TEvent>, configure: TConfiguration.() -> Unit)
}


class Fsm<TEvent : Any, TState : Any>(
    private val stateKey: AttributeKey<TState>,
    private val stateStore: StateStore<TEvent, TState>,
) : DispatcherFeature<TEvent, Unit> {
    override fun install(
        pipeline: EventPipeline<TEvent>,
        configure: Unit.() -> Unit,
    ) {
        pipeline.intercept(EventPipeline.Setup) {
            val state = stateStore.getState(context)
            pipeline.attributes.put(stateKey, state)
        }
    }
}


class HandlersBuilder<TEvent, TEventContext> : Registrar<TEvent, TEventContext> {
    private val handlers = mutableSetOf<HandlerPair<TEvent, TEventContext, *>>()

    override fun <R> register(
        trigger: EventTrigger<TEventContext, R>,
        filter: ResultingFilter<TEvent, R>,
    ) {
        handlers.add(HandlerPair(filter = filter, trigger = trigger))
    }

    fun build(): Iterable<HandlerPair<TEvent, TEventContext, *>> = handlers
}


class EventHandling<TEvent : Any, TEventContext>(
    private val triggerOnlyFirst: Boolean = true,
    private val createEventContext: suspend (TEvent) -> TEventContext,
) : DispatcherFeature<TEvent, Registrar<TEvent, TEventContext>> {
    override fun install(
        pipeline: EventPipeline<TEvent>,
        configure: Registrar<TEvent, TEventContext>.() -> Unit,
    ) {
        val handlers = HandlersBuilder<TEvent, TEventContext>().apply(configure).build()

        pipeline.intercept(EventPipeline.Event) {
            val event = context
            val eventContext = createEventContext(event)
            for (handler in handlers) {
                // TODO: parallelize this flow iterations via markers
                val result = handler.filterAndTrigger(event, eventContext)
                if (triggerOnlyFirst && result.isNotEmpty()) {
                    break
                }
            }
        }
    }
}

class Logging<TEvent : Any> : DispatcherFeature<TEvent, Unit> {
    override fun install(pipeline: EventPipeline<TEvent>, configure: Unit.() -> Unit) {
        pipeline.intercept(EventPipeline.Monitoring) {
            logger.info { "Incoming event: $context" }
        }
    }
}


class BaseDispatcher<TEvent : Any>(
    private val scope: CoroutineScope,
    private val eventFlow: Flow<TEvent>,
) : Dispatcher<TEvent> {
    private val pipeline = EventPipeline<TEvent>()

    fun start() =
        eventFlow
            .onEach { pipeline.execute(it) }
            .launchIn(scope)  // TODO: Check if flow is already started

    fun stop() {
        scope.cancel()
    }

    override fun <TConfiguration> install(
        feature: DispatcherFeature<TEvent, TConfiguration>,
        configure: TConfiguration.() -> Unit,
    ) {
        // TODO: Check if a feature is already installed
        feature.install(pipeline, configure)
    }
}

val logger = KotlinLogging.logger { }

enum class StateValues {
    INIT
}

class MemoryStateStore<C> : StateStore<C, StateValues> {
    override suspend fun getState(context: C): StateValues {
        return StateValues.INIT
    }
}


@OptIn(PreviewFeature::class)
fun main() = runBlocking(Dispatchers.IO) {
    val bot = telegramBot(System.getenv("BOT_TOKEN"))

    val dispatcher = BaseDispatcher(
        scope = this,
        eventFlow = bot.longPollingFlow(),
    )

    val stateKey = AttributeKey<StateValues>("StateKey")

    dispatcher.apply {
        install(Logging())

        install(
            EventHandling { TelegramSendingContext(bot, it.sourceChat()?.id!!) }
        ) {
            onText("hi", ignoreCase = true) {
                sendMessage("Oh, hello")
            }

            onText("name") {
                sendMessage("Your name: ${getChat(chatId).asPrivateChat()?.firstName}")
            }

            onText {
                sendMessage("Last handler")
            }
        }

        // TODO: Turn this back and test it
//        install(
//            Fsm(stateKey = stateKey, stateStore = MemoryStateStore())
//        )
    }

    dispatcher.start()
    logger.info { "Application started" }
}



typealias TelegramEventTrigger<E> = EventTrigger<TelegramEventContext, E>
typealias BooleanFilter <E> = suspend (E) -> Boolean
// Iterable is used here because we can get multiple filtering results from one incoming event
typealias ResultingFilter<E, R> = suspend (E) -> Option<Iterable<R>>


fun <C> TelegramRegistrar<C>.onText(
    text: String,
    ignoreCase: Boolean = false,
    trigger: EventTrigger<C, CommonMessage<TextContent>>,
) {
    onText({ text.equals(it.content.text, ignoreCase = ignoreCase) }, trigger)
}

fun <C> TelegramRegistrar<C>.onText(
    trigger: EventTrigger<C, CommonMessage<TextContent>>,
) {
    onText(filter = { true }, trigger)
}


fun <C> TelegramRegistrar<C>.onText(
    filter: BooleanFilter<CommonMessage<TextContent>>,
    trigger: EventTrigger<C, CommonMessage<TextContent>>,
) {
    onContent(includeMediaGroups = true, filter = filter, trigger = trigger)
}


@OptIn(PreviewFeature::class)
internal inline fun <reified T : MessageContent, C> TelegramRegistrar<C>.onContent(
    includeMediaGroups: Boolean,
    noinline filter: BooleanFilter<CommonMessage<T>>,
    noinline trigger: EventTrigger<C, CommonMessage<T>>,
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
                }?.let {
                    return@register it.toOption()
                }
        }

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
