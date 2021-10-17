//import MyTokens.*
//import adapters.telegram.*
//import dev.inmo.tgbotapi.bot.Ktor.telegramBot
//import framework.*
//import kotlinx.coroutines.sync.Mutex
//import kotlinx.coroutines.sync.withLock
//
//enum class MyTokens(val value: String) {
//    GREETING("GREETING"),
//    MAIN_MENU("MAIN_MENU")
//}
//
////class MyMemoryStateStore(private val primaryState: MyTokens) : StateStore<MyTokens, TelegramEventContext> {
////    private val stateMap = mutableMapOf<TelegramEventContext, MyTokens>()
////    private val stateMapMutex = Mutex()
////
////    override fun compareStates(one: MyTokens, other: MyTokens) = one == other
////
////    override suspend fun setState(context: TelegramEventContext, token: MyTokens) =
////        stateMapMutex.withLock { stateMap[context] = token }
////
////    override suspend fun getState(context: TelegramEventContext) =
////        stateMapMutex.withLock { stateMap.getOrElse(context) { primaryState } }
//}
//
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
//}
//
//data class Handler<E>(
//    val trigger: suspend (E) -> Unit,
//    val filter: Filter<E>,
//)
//
//
//interface FilterOrchestrator<E : Any> {
//    suspend fun processEvent(event: E)
//}
//
//
//suspend fun <E> Collection<Handler<E>>.check(event: E) {
//    for (handler in this) {
//        if (handler.filter.validate(event)) {
//            handler.trigger(event)
//            break
//        }
//    }
//}
//
//// TODO: Find state
//class TelegramFilterOrchestrator(
//    private val textHandlers: List<Handler<TextEvent>>,
//) : FilterOrchestrator<TelegramEvent> {
//    override suspend fun processEvent(event: TelegramEvent) {
//        when (event) {
//            is TextEvent -> textHandlers.check(event)
//        }
//    }
//}
//
//
//interface Filter<E> {
//    fun validate(event: E): Boolean
//}
//
//class TextFilter(private val matchString: String) : Filter<TextEvent> {
//    override fun validate(event: TextEvent): Boolean = matchString == event.text
//}
//
//sealed interface TelegramEvent
//
//data class TextEvent(val text: String) : TelegramEvent